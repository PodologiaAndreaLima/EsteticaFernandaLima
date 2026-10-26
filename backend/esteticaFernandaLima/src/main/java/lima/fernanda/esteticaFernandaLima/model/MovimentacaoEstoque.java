package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private NotaFiscalItem item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private ServicoProduto produto;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "custo_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "estoque_apos", nullable = false)
    private int estoqueApos;

    @Column(name = "valor_venda_apos", precision = 19, scale = 2)
    private BigDecimal valorVendaApos;

    @Column(name = "registrada_em", nullable = false)
    private Instant registradaEm;
}
