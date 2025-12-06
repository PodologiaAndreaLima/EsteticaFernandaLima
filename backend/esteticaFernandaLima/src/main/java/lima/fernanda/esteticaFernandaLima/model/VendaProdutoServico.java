package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

@Entity
public class VendaProdutoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVendaProdutoServico;

    @ManyToOne
    @JoinColumn(name = "fk_ordem_servico")
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "fk_servico_produto")
    private ServicoProduto servicoProduto;

    @ManyToOne
    @JoinColumn(name = "fk_combo")
    private Combo combo;

    private Float desconto;
    private Integer quantidade;



    public Integer getIdVendaProdutoServico() {
        return idVendaProdutoServico;
    }

    public void setIdVendaProdutoServico(Integer idVendaProdutoServico) {
        this.idVendaProdutoServico = idVendaProdutoServico;
    }

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public ServicoProduto getServicoProduto() {
        return servicoProduto;
    }

    public void setServicoProduto(ServicoProduto servicoProduto) {
        this.servicoProduto = servicoProduto;
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        this.combo = combo;
    }
}
