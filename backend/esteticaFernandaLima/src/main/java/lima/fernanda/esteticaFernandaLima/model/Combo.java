package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(schema = "combo")
public class Combo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCombo;
    private String nome;
    private String descricao;
    private Float valorFinal;

    @OneToMany(mappedBy = "combo")
    private List<VendaProdutoServico> vendas;

    @OneToMany(mappedBy = "combo")
    private List<ComboServicoProduto> itensCombo;

    public Integer getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(Integer idCombo) {
        this.idCombo = idCombo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Float getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Float valorFinal) {
        this.valorFinal = valorFinal;
    }

    public List<VendaProdutoServico> getVendas() {
        return vendas;
    }

    public List<ComboServicoProduto> getItensCombo() {
        return itensCombo;
    }
}
