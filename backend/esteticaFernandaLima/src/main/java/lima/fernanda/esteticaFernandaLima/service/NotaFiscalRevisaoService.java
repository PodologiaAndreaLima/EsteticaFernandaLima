package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato;
import lima.fernanda.esteticaFernandaLima.etl.nota.LimpezaImagemNota;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.*;
import lima.fernanda.esteticaFernandaLima.model.*;
import lima.fernanda.esteticaFernandaLima.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class NotaFiscalRevisaoService {
    private final NotaFiscalImportacaoRepository notas;
    private final NotaFiscalItemRepository itens;
    private final MovimentacaoEstoqueRepository movimentos;
    private final NotaFiscalIdentidadeRepository identidades;
    private final LimpezaImagemNota limpeza;

    public NotaFiscalRevisaoService(NotaFiscalImportacaoRepository notas, NotaFiscalItemRepository itens,
                                   MovimentacaoEstoqueRepository movimentos, NotaFiscalIdentidadeRepository identidades,
                                   LimpezaImagemNota limpeza) {
        this.notas = notas;
        this.itens = itens;
        this.movimentos = movimentos;
        this.identidades = identidades;
        this.limpeza = limpeza;
    }

    @Transactional(readOnly = true)
    public NotaResposta consultar(Long id, Long usuarioId) {
        var nota = notas.findByIdAndUsuarioId(id, usuarioId).orElseThrow(() ->
                new NotaFiscalErro(HttpStatus.NOT_FOUND, CodigoErro.NOTA_INVALIDA, null));
        var linhas = itens.findByNotaIdAndNotaUsuarioIdOrderByOrdemAsc(id, usuarioId);
        var resposta = linhas.stream().map(item -> mapearItem(item, usuarioId)).toList();
        var resumo = nota.getStatus() == EstadoNota.CONFIRMADA ? resumo(nota, linhas, usuarioId) : null;
        var fornecedor = nota.getFornecedorCnpj() == null && nota.getFornecedorNome() == null ? null :
                new Fornecedor(nota.getFornecedorCnpj(), nota.getFornecedorNome());
        return new NotaResposta(id, nota.getStatus(), fornecedor, nota.getDataEmissao(), nota.getTotalNota(),
                nota.getChaveAcesso(), avisos(nota, linhas), nota.getCodigoFalha(), resposta, resumo);
    }

    private List<String> avisos(NotaFiscalImportacao nota, List<NotaFiscalItem> linhas) {
        List<String> codigos = nota.getAvisosExtracao() == null || nota.getAvisosExtracao().isBlank()
                ? new ArrayList<>() : new ArrayList<>(List.of(nota.getAvisosExtracao().split(",")));
        if (nota.getTotalNota() == null || linhas.isEmpty() || linhas.stream().anyMatch(i -> i.getValorTotal() == null)) {
            return List.copyOf(codigos);
        }
        BigDecimal soma = linhas.stream().map(NotaFiscalItem::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (soma.compareTo(nota.getTotalNota()) != 0 && !codigos.contains("TOTAL_DIVERGENTE")) codigos.add("TOTAL_DIVERGENTE");
        return List.copyOf(codigos);
    }

    private Item mapearItem(NotaFiscalItem item, Long usuarioId) {
        var produto = item.getProdutoSugerido();
        ProdutoSugerido sugestao = produto != null && produto.getUsuario() != null
                && usuarioId.equals(produto.getUsuario().getId()) && Boolean.TRUE.equals(produto.getAtivo())
                && Boolean.TRUE.equals(produto.getProduto())
                ? new ProdutoSugerido(produto.getIdProdutoServico().longValue(), produto.getNome()) : null;
        Rejeicao motivo = item.getMotivoRejeicao() == null ? null :
                new Rejeicao(item.getMotivoRejeicao(), item.getMotivoRejeicao().name());
        return new Item(item.getId(), item.getTextoOriginal(), item.getQuantidade(), item.getValorUnitario(),
                item.getValorTotal(), sugestao, item.getConfianca(), item.getStatus(), motivo);
    }

    @Transactional
    public void descartar(Long id, Long usuarioId) {
        var nota = notas.bloquearPropria(id, usuarioId).orElseThrow(() ->
                new NotaFiscalErro(HttpStatus.NOT_FOUND, CodigoErro.NOTA_INVALIDA, null));
        if (nota.getStatus() != EstadoNota.PROCESSANDO && nota.getStatus() != EstadoNota.EM_REVISAO) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, nota.getStatus());
        }
        if (notas.finalizar(id, usuarioId, nota.getVersao(),
                List.of(EstadoNota.PROCESSANDO, EstadoNota.EM_REVISAO), EstadoNota.DESCARTADA, null, Instant.now()) != 1) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, nota.getStatus());
        }
        identidades.liberarDescartada(id, usuarioId);
        limpeza.aposCommit(id);
    }

    @Transactional(readOnly = true)
    public ResumoConfirmacao resumo(NotaFiscalImportacao nota, List<NotaFiscalItem> linhas, Long usuarioId) {
        var entradas = movimentos.findByItemNotaIdAndItemNotaUsuarioIdOrderByItemOrdemAsc(nota.getId(), usuarioId);
        var confirmados = entradas.stream().map(m -> new ItemConfirmado(m.getItem().getId(),
                m.getProduto().getIdProdutoServico().longValue(), m.getQuantidade(), m.getCustoUnitario())).toList();
        var rejeitados = linhas.stream().filter(i -> i.getStatus() == EstadoItem.REJEITADO)
                .map(i -> new ItemRejeitado(i.getId(), i.getMotivoRejeicao())).toList();
        Map<Long, ProdutoAfetado> afetados = new LinkedHashMap<>();
        for (var movimento : entradas) {
            long produtoId = movimento.getProduto().getIdProdutoServico().longValue();
            afetados.put(produtoId, new ProdutoAfetado(produtoId, movimento.getEstoqueApos(),
                    movimento.getCustoUnitario(), movimento.getValorVendaApos()));
        }
        return new ResumoConfirmacao(nota.getId(), EstadoNota.CONFIRMADA, confirmados, rejeitados,
                List.copyOf(afetados.values()));
    }
}
