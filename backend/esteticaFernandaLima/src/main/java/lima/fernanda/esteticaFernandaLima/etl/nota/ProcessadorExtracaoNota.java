package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoItem;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.MotivoRejeicao;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNota;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaClient;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaException;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalIdentidade.Tipo;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalItem;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalIdentidadeRepository;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalImportacaoRepository;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalItemRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Conditional;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ChaveGeminiDisponivel;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.List;

@Component
@Conditional(ChaveGeminiDisponivel.class)
public final class ProcessadorExtracaoNota implements ProcessadorImagemNota {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessadorExtracaoNota.class);
    private final ExtracaoNotaClient cliente;
    private final NotaFiscalImportacaoRepository notas;
    private final NotaFiscalItemRepository itens;
    private final NotaFiscalIdentidadeRepository identidades;
    private final ServicoProdutoRepository produtos;
    private final TransactionTemplate transacao;
    private final NormalizadorExtracaoNota normalizador = new NormalizadorExtracaoNota(Clock.systemUTC());
    private final PareadorProdutoNota pareador = new PareadorProdutoNota();

    public ProcessadorExtracaoNota(ExtracaoNotaClient cliente, NotaFiscalImportacaoRepository notas,
                                   NotaFiscalItemRepository itens, NotaFiscalIdentidadeRepository identidades,
                                   ServicoProdutoRepository produtos, TransactionTemplate transacao) {
        this.cliente = cliente;
        this.notas = notas;
        this.itens = itens;
        this.identidades = identidades;
        this.produtos = produtos;
        this.transacao = transacao;
    }

    @Override
    public boolean disponivel() { return true; }

    @Override
    public void processar(Long notaId, Long usuarioId, byte[] imagem, String mime) {
        var extracao = cliente.extrair(imagem, mime);
        String chaveGemini = extracao.chaveAcesso();
        // O QR code da NFC-e, quando legível e com dígito verificador válido, é a fonte da chave de acesso.
        var chaveQr = LeitorQrNota.lerChave(imagem).filter(NormalizadorExtracaoNota::chaveValida);
        if (chaveQr.isPresent()) {
            extracao = new ExtracaoNota(extracao.legivel(), extracao.motivo(), extracao.fornecedor(),
                    extracao.dataEmissao(), extracao.totalNota(), chaveQr.get(), extracao.itens());
        }
        boolean divergente = chaveQr.isPresent() && chaveGemini != null
                && !chaveGemini.replaceAll("\\D", "").equals(chaveQr.get());
        var validada = comAvisoDivergencia(normalizador.validar(extracao), divergente);
        registrarOrigemDaChave(notaId, chaveQr.isPresent(), divergente, validada);
        List<ServicoProduto> candidatos = produtos.findByUsuarioIdAndProdutoTrueAndAtivoTrueOrderByIdProdutoServicoAsc(usuarioId)
                .stream().filter(p -> p.getUsuario() != null && usuarioId.equals(p.getUsuario().getId())).toList();
        try {
            transacao.executeWithoutResult(status -> publicar(notaId, usuarioId, validada, candidatos));
        } catch (DataIntegrityViolationException colisao) {
            if (validada.chave() != null && identidades.findByUsuarioIdAndTipoIdentidadeAndIdentidadeDigest(
                    usuarioId, Tipo.CHAVE_ACESSO, digest(validada.chave()))
                    .filter(i -> !i.getNota().getId().equals(notaId)).isPresent()) {
                throw new ExtracaoNotaException(CodigoFalha.DUPLICADA);
            }
            throw colisao;
        }
    }

    /** Diagnostico: de onde veio a chave de acesso. Nunca registra o valor da chave. */
    private static void registrarOrigemDaChave(Long notaId, boolean doQr, boolean divergente,
                                               NormalizadorExtracaoNota.Nota dados) {
        if (doQr) {
            LOG.info("Nota {}: chave de acesso obtida do QR Code{}", notaId,
                    divergente ? " (a leitura do texto impresso divergia e foi descartada)" : "");
        } else if (dados.chave() != null) {
            LOG.info("Nota {}: chave de acesso obtida do texto impresso (QR Code ausente ou ilegivel)", notaId);
        } else if (dados.avisos().contains("CHAVE_NAO_CONFIRMADA")) {
            LOG.warn("Nota {}: chave de acesso nao confirmada (QR Code ilegivel e texto impresso invalido); seguindo sem chave", notaId);
        } else {
            LOG.info("Nota {}: sem chave de acesso (QR Code ausente e nenhuma chave lida no texto)", notaId);
        }
    }

    private static NormalizadorExtracaoNota.Nota comAvisoDivergencia(NormalizadorExtracaoNota.Nota dados, boolean divergente) {
        if (!divergente) return dados;
        List<String> avisos = new java.util.ArrayList<>(dados.avisos());
        avisos.add("CHAVE_DIVERGENTE");
        return new NormalizadorExtracaoNota.Nota(dados.cnpj(), dados.fornecedor(), dados.data(), dados.total(),
                dados.chave(), dados.linhas(), List.copyOf(avisos));
    }

    private void publicar(Long id, Long usuarioId, NormalizadorExtracaoNota.Nota dados,
                         List<ServicoProduto> candidatos) {
        var nota = notas.bloquearPropria(id, usuarioId).orElseThrow(() -> new IllegalStateException("Job sem nota propria"));
        if (nota.getStatus() != EstadoNota.PROCESSANDO) return;
        if (dados.chave() != null) {
            String digest = digest(dados.chave());
            var existente = identidades.findByUsuarioIdAndTipoIdentidadeAndIdentidadeDigest(usuarioId, Tipo.CHAVE_ACESSO, digest);
            if (existente.isPresent() && !existente.get().getNota().getId().equals(id)) {
                throw new ExtracaoNotaException(CodigoFalha.DUPLICADA);
            }
            if (existente.isEmpty()) identidades.reservar(notas, id, usuarioId, Tipo.CHAVE_ACESSO, digest);
        }
        nota.setFornecedorCnpj(dados.cnpj());
        nota.setFornecedorNome(dados.fornecedor());
        List<String> avisos = new java.util.ArrayList<>(dados.avisos());
        if (dados.cnpj() != null && dados.data() != null && dados.total() != null
                && notas.contarConfirmadaSemelhante(usuarioId, id, dados.cnpj(), dados.data(), dados.total()) > 0) {
            // Sem chave de acesso nao ha como provar duplicidade: a usuaria decide na revisao.
            avisos.add("POSSIVEL_DUPLICATA");
        }
        nota.setAvisosExtracao(String.join(",", avisos));
        nota.setDataEmissao(dados.data());
        nota.setTotalNota(dados.total());
        nota.setChaveAcesso(dados.chave());
        notas.saveAndFlush(nota);
        for (int pos = 0; pos < dados.linhas().size(); pos++) salvarLinha(nota, pos, dados.linhas().get(pos), candidatos);
        if (notas.publicarRevisao(id, usuarioId, nota.getVersao()) != 1) throw new IllegalStateException("Revisao concorrente");
    }

    private void salvarLinha(lima.fernanda.esteticaFernandaLima.model.NotaFiscalImportacao nota, int ordem,
                             NormalizadorExtracaoNota.Linha dados, List<ServicoProduto> candidatos) {
        NotaFiscalItem item = new NotaFiscalItem();
        item.setNota(nota);
        item.setOrdem(ordem);
        item.setTextoOriginal(dados.texto());
        item.setQuantidade(dados.quantidade());
        item.setValorUnitario(dados.unitario());
        item.setValorTotal(dados.total());
        item.setStatus(dados.valida() ? EstadoItem.VALIDO : EstadoItem.INVALIDO);
        if (!dados.valida()) item.setMotivoRejeicao(MotivoRejeicao.DADOS_INVALIDOS);
        if (dados.valida()) {
            var sugestao = pareador.sugerir(dados.texto(), candidatos);
            if (sugestao != null) {
                item.setProdutoSugerido(sugestao.produto());
                item.setConfianca(sugestao.confianca());
            }
        }
        itens.save(item);
    }

    private static String digest(String chave) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(chave.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException falha) {
            throw new IllegalStateException("SHA-256 indisponivel", falha);
        }
    }
}
