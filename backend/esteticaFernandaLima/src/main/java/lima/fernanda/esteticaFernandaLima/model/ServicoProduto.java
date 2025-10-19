package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

@Entity
@Table
public class ServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProdutoServico;

    private Boolean isProduto;
    private String nome;
    private Float despesa;
    private Float valorVenda;
    private String descricao;

    public Integer getIdProdutoServico() {
        return idProdutoServico;
    }

    public void setIdProdutoServico(Integer idProdutoServico) {
        this.idProdutoServico = idProdutoServico;
    }

    public Boolean getProduto() {
        return isProduto;
    }

    public void setProduto(Boolean produto) {
        isProduto = produto;
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
}
