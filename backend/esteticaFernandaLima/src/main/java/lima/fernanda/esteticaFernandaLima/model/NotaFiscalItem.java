package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.Confianca;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoItem;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.MotivoRejeicao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "nota_fiscal_item", uniqueConstraints =
        @UniqueConstraint(name = "uq_nota_item_ordem", columnNames = {"nota_id", "ordem"}))
public class NotaFiscalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nota_id", nullable = false)
    private NotaFiscalImportacao nota;

    @Column(nullable = false)
    private int ordem;

    @Column(name = "texto_original", nullable = false, length = 500)
    private String textoOriginal;

    private Integer quantidade;

    @Column(name = "valor_unitario", precision = 19, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_sugerido_id")
    private ServicoProduto produtoSugerido;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Confianca confianca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoItem status = EstadoItem.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_rejeicao", length = 40)
    private MotivoRejeicao motivoRejeicao;

    @Column(name = "rejeicao_texto", length = 160)
    private String rejeicaoTexto;
}
