package lima.fernanda.esteticaFernandaLima.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "venda_produto_servico")
public class VendaProdutoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVendaProdutoServico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_servico_produto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServicoProduto servicoProduto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_combo")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Combo combo;

    private Float desconto;
    private Integer quantidade;

    public Integer getIdVendaProdutoServico() { return idVendaProdutoServico; }
    public void setIdVendaProdutoServico(Integer idVendaProdutoServico) { this.idVendaProdutoServico = idVendaProdutoServico; }


    public ServicoProduto getServicoProduto() { return servicoProduto; }
    public void setServicoProduto(ServicoProduto servicoProduto) { this.servicoProduto = servicoProduto; }

    public Combo getCombo() { return combo; }
    public void setCombo(Combo combo) { this.combo = combo; }

    public Float getDesconto() { return desconto; }
    public void setDesconto(Float desconto) { this.desconto = desconto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}