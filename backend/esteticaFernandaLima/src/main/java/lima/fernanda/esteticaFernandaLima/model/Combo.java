package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

import java.util.ArrayList;
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
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<VendaProdutoServico> vendas;

    @Column(nullable = false, columnDefinition = "float default 0")
    private Float desconto;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo = true;

    // antes: @OneToMany(mappedBy = "combo") sem cascade e sem inicializar
    @OneToMany(mappedBy = "combo", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ComboServicoProduto> itensCombo = new ArrayList<>();

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

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<VendaProdutoServico> getVendas() {
        return vendas;
    }

    public List<ComboServicoProduto> getItensCombo() {
        return itensCombo;
    }
}
