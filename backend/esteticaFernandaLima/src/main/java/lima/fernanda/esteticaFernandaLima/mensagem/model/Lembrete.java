package lima.fernanda.esteticaFernandaLima.mensagem.model;

import jakarta.persistence.*;
import lima.fernanda.esteticaFernandaLima.model.Cliente;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "lembrete",
        uniqueConstraints = @UniqueConstraint(name = "uk_lembrete_clinica_cliente_horario_tipo",
                columnNames = {"clinica_id", "cliente_id", "data_hora", "tipo"}))
public class Lembrete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clinica_id", nullable = false, length = 100)
    private String clinicaId = "default";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "agendamento_id")
    private Long agendamentoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "data_ultimo_atendimento")
    private LocalDate dataUltimoAtendimento;

    @Column(name = "data_retorno_previsto")
    private LocalDate dataRetornoPrevisto;

    @Column(name = "procedimento", length = 300)
    private String procedimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoMensagem tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusLembrete status = StatusLembrete.PENDENTE;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void onCreate() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
        if (clinicaId == null || clinicaId.isBlank()) clinicaId = "default";
        if (status == null) status = StatusLembrete.PENDENTE;
    }

    public Long getId() { return id; }
    public String getClinicaId() { return clinicaId; }
    public Cliente getCliente() { return cliente; }
    public Long getAgendamentoId() { return agendamentoId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public LocalDate getDataUltimoAtendimento() { return dataUltimoAtendimento; }
    public LocalDate getDataRetornoPrevisto() { return dataRetornoPrevisto; }
    public String getProcedimento() { return procedimento; }
    public TipoMensagem getTipo() { return tipo; }
    public StatusLembrete getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setId(Long id) { this.id = id; }
    public void setClinicaId(String clinicaId) { this.clinicaId = clinicaId; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setAgendamentoId(Long agendamentoId) { this.agendamentoId = agendamentoId; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public void setDataUltimoAtendimento(LocalDate dataUltimoAtendimento) { this.dataUltimoAtendimento = dataUltimoAtendimento; }
    public void setDataRetornoPrevisto(LocalDate dataRetornoPrevisto) { this.dataRetornoPrevisto = dataRetornoPrevisto; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }
    public void setTipo(TipoMensagem tipo) { this.tipo = tipo; }
    public void setStatus(StatusLembrete status) { this.status = status; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
