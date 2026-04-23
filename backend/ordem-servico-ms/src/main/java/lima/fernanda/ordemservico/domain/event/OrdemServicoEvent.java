package lima.fernanda.ordemservico.domain.event;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Evento de domínio disparado após operações de CRUD em OrdemServico.
 * Inclui os itens da ordem para que o ms-scr possa calcular métricas de dashboard
 * sem precisar consultar o back-scr.
 */
public class OrdemServicoEvent {

    public enum TipoOperacao {
        CREATE, UPDATE, DELETE
    }

    public static class ItemEvent {
        private Integer servicoProdutoId;
        private Integer comboId;
        private Integer produtoId;
        private Integer quantidade;
        private Float desconto;
        private String nomeServicoProduto;
        private String nomeCombo;
        private Boolean ehProduto;

        public ItemEvent() {}

        public ItemEvent(Integer servicoProdutoId, Integer comboId, Integer produtoId,
                         Integer quantidade, Float desconto,
                         String nomeServicoProduto, String nomeCombo, Boolean ehProduto) {
            this.servicoProdutoId = servicoProdutoId;
            this.comboId = comboId;
            this.produtoId = produtoId;
            this.quantidade = quantidade;
            this.desconto = desconto;
            this.nomeServicoProduto = nomeServicoProduto;
            this.nomeCombo = nomeCombo;
            this.ehProduto = ehProduto;
        }

        public Integer getServicoProdutoId() { return servicoProdutoId; }
        public void setServicoProdutoId(Integer id) { this.servicoProdutoId = id; }
        public Integer getComboId() { return comboId; }
        public void setComboId(Integer id) { this.comboId = id; }
        public Integer getProdutoId() { return produtoId; }
        public void setProdutoId(Integer id) { this.produtoId = id; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer q) { this.quantidade = q; }
        public Float getDesconto() { return desconto; }
        public void setDesconto(Float d) { this.desconto = d; }
        public String getNomeServicoProduto() { return nomeServicoProduto; }
        public void setNomeServicoProduto(String n) { this.nomeServicoProduto = n; }
        public String getNomeCombo() { return nomeCombo; }
        public void setNomeCombo(String n) { this.nomeCombo = n; }
        public Boolean getEhProduto() { return ehProduto; }
        public void setEhProduto(Boolean ep) { this.ehProduto = ep; }
    }

    private Integer ordemServicoId;
    private Integer clienteId;
    private Integer usuarioId;
    private Float valorFinal;
    private LocalDate dataCriacao;
    private TipoOperacao tipoOperacao;
    private List<ItemEvent> itens = new ArrayList<>();

    public OrdemServicoEvent() {}

    public OrdemServicoEvent(Integer ordemServicoId, Integer clienteId, Integer usuarioId,
                             Float valorFinal, LocalDate dataCriacao, TipoOperacao tipoOperacao) {
        this.ordemServicoId = ordemServicoId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.valorFinal = valorFinal;
        this.dataCriacao = dataCriacao;
        this.tipoOperacao = tipoOperacao;
    }

    public OrdemServicoEvent(Integer ordemServicoId, Integer clienteId, Integer usuarioId,
                             Float valorFinal, LocalDate dataCriacao, TipoOperacao tipoOperacao,
                             List<ItemEvent> itens) {
        this(ordemServicoId, clienteId, usuarioId, valorFinal, dataCriacao, tipoOperacao);
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    public Integer getOrdemServicoId() { return ordemServicoId; }
    public void setOrdemServicoId(Integer id) { this.ordemServicoId = id; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer id) { this.clienteId = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer id) { this.usuarioId = id; }
    public Float getValorFinal() { return valorFinal; }
    public void setValorFinal(Float v) { this.valorFinal = v; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate d) { this.dataCriacao = d; }
    public TipoOperacao getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(TipoOperacao t) { this.tipoOperacao = t; }
    public List<ItemEvent> getItens() { return itens; }
    public void setItens(List<ItemEvent> itens) { this.itens = itens != null ? itens : new ArrayList<>(); }

    @Override
    public String toString() {
        return "OrdemServicoEvent{ordemServicoId=" + ordemServicoId +
                ", tipoOperacao=" + tipoOperacao + ", itens=" + (itens != null ? itens.size() : 0) + '}';
    }
}
