package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "nota_fiscal_job")
public class NotaFiscalJob {
    public enum Estado { PENDENTE, EM_EXECUCAO, CONCLUIDO, CANCELADO }

    @Id
    @Column(name = "nota_id")
    private Long notaId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "nota_id")
    private NotaFiscalImportacao nota;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado status = Estado.PENDENTE;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "lease_ate")
    private Instant leaseAte;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
