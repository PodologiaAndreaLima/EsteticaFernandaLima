package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrdemServico;

    private Float valorFinal;

    private LocalDate dtHora;

    private String observacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_cliente", nullable = false)
    @JsonIgnoreProperties({"ordensServico", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_usuario", nullable = false)
    @JsonIgnoreProperties({"ordensServico", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VendaProdutoServico> itens = new ArrayList<>();

    public Integer getIdOrdemServico() { return idOrdemServico; }
    public void setIdOrdemServico(Integer idOrdemServico) { this.idOrdemServico = idOrdemServico; }

    public Float getValorFinal() { return valorFinal; }
    public void setValorFinal(Float valorFinal) { this.valorFinal = valorFinal; }

    public LocalDate getDtHora() { return dtHora; }
    public void setDtHora(LocalDate dtHora) { this.dtHora = dtHora; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<VendaProdutoServico> getItens() { return itens; }
    public void setItens(List<VendaProdutoServico> itens) {
        this.itens.clear();
        if (itens != null) {
            itens.forEach(this::addItem);
        }
    }

    public void addItem(VendaProdutoServico item) {
        item.setOrdemServico(this);
        this.itens.add(item);
    }

    public void removeItem(VendaProdutoServico item) {
        item.setOrdemServico(null);
        this.itens.remove(item);
    }
}