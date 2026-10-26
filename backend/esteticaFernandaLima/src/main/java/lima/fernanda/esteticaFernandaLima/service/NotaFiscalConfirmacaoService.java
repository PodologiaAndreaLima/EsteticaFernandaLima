package lima.fernanda.esteticaFernandaLima.service;

import jakarta.persistence.EntityManager;
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
import java.util.stream.Collectors;

@Service
public class NotaFiscalConfirmacaoService {
    private final NotaFiscalImportacaoRepository notas;
    private final NotaFiscalItemRepository itens;
    private final ServicoProdutoRepository produtos;
    private final MovimentacaoEstoqueRepository movimentos;
    private final NotaFiscalRevisaoService revisao;
    private final EntityManager em;
    private final LimpezaImagemNota limpeza;

    public NotaFiscalConfirmacaoService(NotaFiscalImportacaoRepository notas, NotaFiscalItemRepository itens,
                                        ServicoProdutoRepository produtos, MovimentacaoEstoqueRepository movimentos,
                                         NotaFiscalRevisaoService revisao, EntityManager em, LimpezaImagemNota limpeza) {
        this.notas = notas;
        this.itens = itens;
        this.produtos = produtos;
        this.movimentos = movimentos;
        this.revisao = revisao;
        this.em = em;
        this.limpeza = limpeza;
    }

    @Transactional
    public ResumoConfirmacao confirmar(Long id, Long usuarioId, ConfirmarRequest request) {
        var nota = notas.bloquearPropria(id, usuarioId).orElseThrow(() ->
                new NotaFiscalErro(HttpStatus.NOT_FOUND, CodigoErro.NOTA_INVALIDA, null));
        if (nota.getStatus() != EstadoNota.EM_REVISAO) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, nota.getStatus());
        }
        var linhas = itens.findByNotaIdAndNotaUsuarioIdOrderByOrdemAsc(id, usuarioId);
        Map<Long, DecisaoItem> decisoes = validarLista(request, linhas);
        var ids = decisoes.values().stream().filter(d -> d.acao() == Acao.ASSOCIAR)
                .map(d -> Math.toIntExact(d.produtoId())).distinct().sorted().toList();
        Map<Integer, ServicoProduto> bloqueados = new HashMap<>();
        for (Integer produtoId : ids) {
            var produto = produtos.bloquearProdutoProprio(produtoId, usuarioId).orElseThrow(() -> invalida());
            bloqueados.put(produtoId, produto);
        }
        for (var linha : linhas) {
            aplicar(linha, decisoes.get(linha.getId()), nota, usuarioId, bloqueados);
        }
        itens.flush();
        movimentos.flush();
        if (notas.finalizar(id, usuarioId, nota.getVersao(), List.of(EstadoNota.EM_REVISAO),
                EstadoNota.CONFIRMADA, null, Instant.now()) != 1) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, EstadoNota.EM_REVISAO);
        }
        var resumo = revisao.resumo(nota, linhas, usuarioId);
        limpeza.aposCommit(id);
        return resumo;
    }

    private Map<Long, DecisaoItem> validarLista(ConfirmarRequest request, List<NotaFiscalItem> linhas) {
        if (request == null || request.itens() == null || request.itens().size() != linhas.size() || linhas.isEmpty()) {
            throw invalida();
        }
        var ids = linhas.stream().map(NotaFiscalItem::getId).collect(Collectors.toSet());
        Map<Long, DecisaoItem> decisoes = new HashMap<>();
        for (var decisao : request.itens()) {
            if (decisao == null || decisao.itemId() == null || !ids.contains(decisao.itemId())
                    || decisoes.putIfAbsent(decisao.itemId(), decisao) != null) throw invalida();
            validarDecisao(decisao);
        }
        return decisoes;
    }

    private void validarDecisao(DecisaoItem d) {
        if (d.acao() == null || d.criarNovo() == null) throw invalida();
        if (d.acao() == Acao.REJEITAR) {
            if (d.produtoId() != null || d.criarNovo() || d.nomeNovo() != null
                    || d.quantidade() != null || d.valorUnitario() != null) throw invalida();
            return;
        }
        if (d.acao() == Acao.ASSOCIAR ? d.criarNovo() || d.produtoId() == null
                || d.produtoId() < 1 || d.produtoId() > Integer.MAX_VALUE || d.nomeNovo() != null
                : !d.criarNovo() || d.produtoId() != null || d.nomeNovo() == null
                || d.nomeNovo().isBlank() || d.nomeNovo().length() > 200
                || d.nomeNovo().chars().anyMatch(Character::isISOControl)) throw invalida();
        if (d.quantidade() != null && d.quantidade() <= 0) throw invalida();
        if (d.valorUnitario() != null && (d.valorUnitario().compareTo(new BigDecimal("0.01")) < 0
                || d.valorUnitario().compareTo(new BigDecimal("9999999.99")) > 0
                || d.valorUnitario().scale() > 2)) throw invalida();
    }

    private void aplicar(NotaFiscalItem linha, DecisaoItem d, NotaFiscalImportacao nota,
                        Long usuarioId, Map<Integer, ServicoProduto> bloqueados) {
        if (linha.getStatus() != EstadoItem.VALIDO && linha.getStatus() != EstadoItem.INVALIDO) throw invalida();
        if (d.acao() == Acao.REJEITAR || d.quantidade() == null || d.valorUnitario() == null) {
            if (d.acao() != Acao.REJEITAR && linha.getStatus() != EstadoItem.INVALIDO) throw invalida();
            var motivo = d.acao() != Acao.REJEITAR ? MotivoRejeicao.DADOS_INVALIDOS
                    : linha.getStatus() == EstadoItem.INVALIDO && linha.getMotivoRejeicao() != null
                    ? linha.getMotivoRejeicao() : linha.getProdutoSugerido() == null
                    ? MotivoRejeicao.SEM_PRODUTO : MotivoRejeicao.REJEITADO_PELA_USUARIA;
            linha.setStatus(EstadoItem.REJEITADO);
            linha.setMotivoRejeicao(motivo);
            linha.setRejeicaoTexto(linha.getMotivoRejeicao().name());
            return;
        }
        if (movimentos.existsByItemIdAndItemNotaIdAndItemNotaUsuarioId(linha.getId(), nota.getId(), usuarioId)) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, EstadoNota.EM_REVISAO);
        }
        var produto = d.acao() == Acao.CRIAR_NOVO ? criarProduto(d, nota) : bloqueados.get(d.produtoId().intValue());
        int estoque = atualizarEstoque(produto, d, usuarioId);
        var movimento = new MovimentacaoEstoque();
        movimento.setItem(linha);
        movimento.setProduto(produto);
        movimento.setQuantidade(d.quantidade());
        movimento.setCustoUnitario(d.valorUnitario());
        movimento.setEstoqueApos(estoque);
        movimento.setValorVendaApos(produto.getValorVenda() == null ? null : BigDecimal.valueOf(produto.getValorVenda().doubleValue()));
        movimento.setRegistradaEm(Instant.now());
        movimentos.save(movimento);
        linha.setQuantidade(d.quantidade());
        linha.setValorUnitario(d.valorUnitario());
        linha.setValorTotal(d.valorUnitario().multiply(BigDecimal.valueOf(d.quantidade())));
        linha.setStatus(EstadoItem.CONFIRMADO);
        linha.setMotivoRejeicao(null);
        linha.setRejeicaoTexto(null);
    }

    private ServicoProduto criarProduto(DecisaoItem d, NotaFiscalImportacao nota) {
        var produto = new ServicoProduto();
        produto.setUsuario(nota.getUsuario());
        produto.setNome(d.nomeNovo().trim());
        produto.setProduto(true);
        produto.setAtivo(true);
        produto.setEstoque(0);
        return produtos.saveAndFlush(produto);
    }

    private int atualizarEstoque(ServicoProduto produto, DecisaoItem d, Long usuarioId) {
        if (produto.getEstoque() == null || produto.getEstoque() < 0
                || produto.getEstoque() > Integer.MAX_VALUE - d.quantidade()) throw invalida();
        if (produtos.incrementarEstoqueECusto(produto.getIdProdutoServico(), usuarioId,
                d.quantidade(), d.valorUnitario()) != 1) {
            throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.CONFLITO_ESTADO, EstadoNota.EM_REVISAO);
        }
        em.refresh(produto);
        return produto.getEstoque();
    }

    private NotaFiscalErro invalida() {
        return new NotaFiscalErro(HttpStatus.BAD_REQUEST, CodigoErro.SOLICITACAO_INVALIDA, null);
    }
}
