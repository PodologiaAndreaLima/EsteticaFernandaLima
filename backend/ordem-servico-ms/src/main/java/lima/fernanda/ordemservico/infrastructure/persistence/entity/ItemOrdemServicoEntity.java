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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", referencedColumnName = "id", nullable = false)
    private OrdemServicoEntity ordemServico;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public OrdemServicoEntity getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServicoEntity ordemServico) {
        this.ordemServico = ordemServico;
    }
}
