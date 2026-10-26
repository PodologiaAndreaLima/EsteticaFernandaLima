package lima.fernanda.ordemservico.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_servico_ms")
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "valor_final", nullable = false)
    private Float valorFinal;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "dt_hora", nullable = false, columnDefinition = "datetime")
    private LocalDateTime dtHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private StatusOrdemServico status = StatusOrdemServico.PENDENTE;

    @Column(name = "retorno", nullable = false)
    private boolean retorno;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;
    public LocalDate getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(LocalDate dataRetorno) { this.dataRetorno = dataRetorno; }

    public LocalDateTime getDtHora() { return dtHora; }
    public void setDtHora(LocalDateTime dtHora) { this.dtHora = dtHora; }
    public StatusOrdemServico getStatus() { return status; }
    public void setStatus(StatusOrdemServico status) { this.status = status; }
    public boolean isRetorno() { return retorno; }
    public void setRetorno(boolean retorno) { this.retorno = retorno; }

    @Column(name = "observacao")
    private String observacao;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrdemServicoEntity> itens = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Float getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Float valorFinal) {
        this.valorFinal = valorFinal;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemOrdemServicoEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrdemServicoEntity> itens) {
        this.itens = itens;
    }
}
