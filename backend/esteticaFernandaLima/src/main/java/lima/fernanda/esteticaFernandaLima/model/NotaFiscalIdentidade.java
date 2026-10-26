package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nota_fiscal_identidade", uniqueConstraints =
        @UniqueConstraint(name = "uq_nota_identidade_dono", columnNames =
                {"usuario_id", "tipo_identidade", "identidade_digest"}))
public class NotaFiscalIdentidade {
    public enum Tipo { CHAVE_ACESSO, DADOS_E_IMAGEM, SOMENTE_IMAGEM }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nota_id", nullable = false)
    private NotaFiscalImportacao nota;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identidade", nullable = false, length = 24)
    private Tipo tipoIdentidade;

    @Column(name = "identidade_digest", nullable = false, length = 64)
    private String identidadeDigest;
}
