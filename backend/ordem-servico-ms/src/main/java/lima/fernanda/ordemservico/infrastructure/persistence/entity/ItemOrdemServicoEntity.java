package lima.fernanda.ordemservico.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ordem_servico_item_ms")
public class ItemOrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "servico_produto_id")
    private Integer servicoProdutoId;

    @Column(name = "combo_id")
    private Integer comboId;

    @Column(name = "produto_id")
    private Integer produtoId;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "desconto")
    private Float desconto;

    /** Nome já resolvido do serviço/produto — persiste para reconstituir eventos */
    @Column(name = "nome_servico_produto", length = 200)
    private String nomeServicoProduto;

    /** Nome já resolvido do combo */
    @Column(name = "nome_combo", length = 200)
    private String nomeCombo;

    /** true = produto, false = serviço */
    @Column(name = "eh_produto")
    private Boolean ehProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", referencedColumnName = "id", nullable = false)
    private OrdemServicoEntity ordemServico;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
    public OrdemServicoEntity getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServicoEntity os) { this.ordemServico = os; }
}
