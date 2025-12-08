package lima.fernanda.esteticaFernandaLima.dto;

import java.util.List;

public class OrdemServicoRequest {
    public static class ItemRequest {
        private Integer servicoProdutoId;
        private Integer comboId;
        private Integer produtoId;
        private Integer quantidade;
        private Float desconto;

        public Integer getServicoProdutoId() { return servicoProdutoId; }
        public void setServicoProdutoId(Integer servicoProdutoId) { this.servicoProdutoId = servicoProdutoId; }
        public Integer getComboId() { return comboId; }
        public void setComboId(Integer comboId) { this.comboId = comboId; }
        public Integer getProdutoId() { return produtoId; }
        public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
        public Float getDesconto() { return desconto; }
        public void setDesconto(Float desconto) { this.desconto = desconto; }
    }

    private Integer clienteId;
    private Integer usuarioId;
    private Float valorFinal;
    private String observacao;
    private List<ItemRequest> itens;

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Float getValorFinal() { return valorFinal; }
    public void setValorFinal(Float valorFinal) { this.valorFinal = valorFinal; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<ItemRequest> getItens() { return itens; }
    public void setItens(List<ItemRequest> itens) { this.itens = itens; }
}