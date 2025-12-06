// java
package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDate;
import java.util.List;

@Entity
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrdemServico;
    private Float valorFinal;
    private LocalDate dtHora;
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_cliente", nullable = false)
    @JsonBackReference("cliente-ordem")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    @JsonBackReference("usuario-ordem")
    private Usuario usuario;

    @OneToMany(mappedBy = "ordemServico")
    private List<VendaProdutoServico> itens;

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
    public void setItens(List<VendaProdutoServico> itens) { this.itens = itens; }
}
