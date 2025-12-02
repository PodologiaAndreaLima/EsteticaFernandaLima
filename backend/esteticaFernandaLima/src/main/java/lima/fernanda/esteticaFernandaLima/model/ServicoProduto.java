package lima.fernanda.esteticaFernandaLima.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table
public class ServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProdutoServico;

    @JsonProperty("isProduto")
    private Boolean produto;

    private String nome;
    private Float despesa;
    private Float valorVenda;
    private String descricao;
    private String marca;
    private String categoria;

    public Integer getIdProdutoServico() {
        return idProdutoServico;
    }

    public void setIdProdutoServico(Integer idProdutoServico) {
        this.idProdutoServico = idProdutoServico;
    }

    public Boolean getProduto() {
        return produto;
    }

    public void setProduto(Boolean produto) {
        this.produto = produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Float getDespesa() {
        return despesa;
    }

    public void setDespesa(Float despesa) {
        this.despesa = despesa;
    }

    public Float getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(Float valorVenda) {
        this.valorVenda = valorVenda;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
