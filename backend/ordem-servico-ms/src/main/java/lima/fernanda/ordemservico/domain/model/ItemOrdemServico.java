package lima.fernanda.ordemservico.domain.model;

public class ItemOrdemServico {

    private Integer servicoProdutoId;
    private Integer comboId;
    private Integer produtoId;
    private Integer quantidade;
    private Float desconto;

    public Integer getServicoProdutoId() {
        return servicoProdutoId;
    }

    public void setServicoProdutoId(Integer servicoProdutoId) {
        this.servicoProdutoId = servicoProdutoId;
    }

    public Integer getComboId() {
        return comboId;
    }

    public void setComboId(Integer comboId) {
        this.comboId = comboId;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }
}

