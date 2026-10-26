package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "nota_fiscal_importacao")
public class NotaFiscalImportacao {
    public enum EstadoLimpeza { DISPONIVEL, PENDENTE, FALHOU, EXCLUIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Version
    @Column(nullable = false)
    private Long versao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNota status = EstadoNota.PROCESSANDO;

    @Column(name = "imagem_digest", nullable = false, length = 64)
    private String imagemDigest;

    @Column(name = "imagem_chave", length = 128)
    private String imagemChave;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_limpeza", nullable = false, length = 20)
    private EstadoLimpeza estadoLimpeza = EstadoLimpeza.DISPONIVEL;

    @Column(name = "limpeza_codigo", length = 40)
    private String limpezaCodigo;

    @Column(name = "limpeza_em")
    private Instant limpezaEm;

    @Column(name = "limpeza_tentativas", nullable = false)
    private int limpezaTentativas;

    @Column(name = "fornecedor_cnpj", length = 14)
    private String fornecedorCnpj;

    @Column(name = "fornecedor_nome", length = 200)
    private String fornecedorNome;

    @Column(name = "avisos_extracao", length = 120)
    private String avisosExtracao;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "total_nota", precision = 19, scale = 2)
    private BigDecimal totalNota;

    @Column(name = "chave_acesso", length = 44)
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "codigo_falha", length = 40)
    private CodigoFalha codigoFalha;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    @Column(name = "finalizada_em")
    private Instant finalizadaEm;
}
