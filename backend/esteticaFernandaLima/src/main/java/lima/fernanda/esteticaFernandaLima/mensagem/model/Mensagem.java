package lima.fernanda.esteticaFernandaLima.mensagem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "clinica_id", nullable = false, length = 100)
    private String clinicaId = "default";
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lembrete_id", nullable = false)
    private Lembrete lembrete;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CanalMensagem canal;
    @Column(nullable = false, length = 2000)
    private String conteudo;
    @Column(name = "telefone_destino", nullable = false, length = 30)
    private String telefoneDestino;
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusMensagem status;
    @Column(name = "erro_provedor", length = 500)
    private String erroProvedor;
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @PrePersist
    void onCreate() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
        if (clinicaId == null || clinicaId.isBlank()) clinicaId = "default";
    }
    public Long getId() { return id; }
    public String getClinicaId() { return clinicaId; }
    public Lembrete getLembrete() { return lembrete; }
    public CanalMensagem getCanal() { return canal; }
    public String getConteudo() { return conteudo; }
    public String getTelefoneDestino() { return telefoneDestino; }
    public StatusMensagem getStatus() { return status; }
    public String getErroProvedor() { return erroProvedor; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setId(Long id) { this.id = id; }
    public void setClinicaId(String clinicaId) { this.clinicaId = clinicaId; }
    public void setLembrete(Lembrete lembrete) { this.lembrete = lembrete; }
    public void setCanal(CanalMensagem canal) { this.canal = canal; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public void setTelefoneDestino(String telefoneDestino) { this.telefoneDestino = telefoneDestino; }
    public void setStatus(StatusMensagem status) { this.status = status; }
    public void setErroProvedor(String erroProvedor) { this.erroProvedor = erroProvedor; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}
