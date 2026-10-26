package lima.fernanda.esteticaFernandaLima.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.List;

@Entity
@Table(schema = "servico_produto")
public class ServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProdutoServico;

    private Boolean produto;

    private String nome;
    private Float despesa;
    private Float valorVenda;
    private String descricao;
    private String marca;
    private String categoria;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo = true;

    @Min(value = 0, message = "O estoque não pode ser negativo")
    @Column(nullable = false)
    private Integer estoque;

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"ordensServico", "servicosProdutos", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @OneToMany(mappedBy = "servicoProduto")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<VendaProdutoServico> vendas;

    @OneToMany(mappedBy = "servicoProduto")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ComboServicoProduto> combos;

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

    // Alias para compatibilidade com frontend
    @com.fasterxml.jackson.annotation.JsonGetter("isProduto")
    public Boolean getIsProduto() {
        return produto;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("isProduto")
    public void setIsProduto(Boolean isProduto) {
        this.produto = isProduto;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public List<VendaProdutoServico> getVendas() {
        return vendas;
    }

    public List<ComboServicoProduto> getCombos() {
        return combos;
    }
}
