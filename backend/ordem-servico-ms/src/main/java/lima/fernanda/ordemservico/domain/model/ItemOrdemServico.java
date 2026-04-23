package lima.fernanda.ordemservico.domain.model;

/**
 * Modelo de domínio de um item de ordem de serviço.
 * Campos de nome (nomeServicoProduto, nomeCombo) são preenchidos pelo back
 * no momento da criação/atualização, para que o evento publicado na fila
 * carregue informações suficientes para os cálculos de dashboard.
 */
public class ItemOrdemServico {

    private Integer servicoProdutoId;
    private Integer comboId;
    private Integer produtoId;
    private Integer quantidade;
    private Float desconto;

    // Campos resolvidos pelo back-scr para enriquecer o evento
    private String nomeServicoProduto;
    private String nomeCombo;
    private Boolean ehProduto;

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

    public String getNomeServicoProduto() { return nomeServicoProduto; }
    public void setNomeServicoProduto(String nomeServicoProduto) { this.nomeServicoProduto = nomeServicoProduto; }

    public String getNomeCombo() { return nomeCombo; }
    public void setNomeCombo(String nomeCombo) { this.nomeCombo = nomeCombo; }

    public Boolean getEhProduto() { return ehProduto; }
    public void setEhProduto(Boolean ehProduto) { this.ehProduto = ehProduto; }
}
