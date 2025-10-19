package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class VendaProdutoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVendaProdutoServico;
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
}
