package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoItem;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.MotivoRejeicao;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaClient;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaException;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalIdentidade.Tipo;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalItem;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalIdentidadeRepository;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalImportacaoRepository;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalItemRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
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
        var validada = normalizador.validar(cliente.extrair(imagem, mime));
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
        nota.setAvisosExtracao(String.join(",", dados.avisos()));
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
