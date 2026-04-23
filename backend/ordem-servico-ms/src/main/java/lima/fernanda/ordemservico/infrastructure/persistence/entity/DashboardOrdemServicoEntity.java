package lima.fernanda.ordemservico.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entidade JPA para persistência do Dashboard de OrdemServico.
 * Armazena dados pré-computados de cada ordem para consultas rápidas.
 * A coluna itens_json armazena a lista de itens serializada em JSON para
 * evitar tabela extra e permitir queries analíticas via JSON_EXTRACT (MySQL 5.7+).
 */
@Entity
@Table(name = "dashboard_ordem_servico_ms", indexes = {
        @Index(name = "idx_dashboard_ordem_servico_id", columnList = "ordem_servico_id"),
        @Index(name = "idx_dashboard_mes_ano", columnList = "mes, ano"),
        @Index(name = "idx_dashboard_usuario_id", columnList = "usuario_id")
})
public class DashboardOrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ordem_servico_id", nullable = false, unique = true)
    private Integer ordemServicoId;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "valor_final", nullable = false)
    private Float valorFinal;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // ── Campos derivados para acesso rápido sem recalcular ──────────────────

    /** Mês extraído de data_criacao — usado para filtros de dashboard */
    @Column(name = "mes", nullable = false)
    private Integer mes;

    /** Ano extraído de data_criacao */
    @Column(name = "ano", nullable = false)
    private Integer ano;

    /** Dia da semana em português (ex: "Segunda", "Terça") */
    @Column(name = "dia_semana", nullable = false, length = 15)
    private String diaSemana;

    /**
     * Itens da ordem serializados em JSON.
     * Formato: [{"nomeItem":"Hidratação","tipo":"SERVICO","quantidade":1}, ...]
     * Permite reconstruir topServicos/topProdutos sem tabela extra.
     */
    @Column(name = "itens_json", columnDefinition = "TEXT")
    private String itensJson;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrdemServicoId() { return ordemServicoId; }
    public void setOrdemServicoId(Integer ordemServicoId) { this.ordemServicoId = ordemServicoId; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Float getValorFinal() { return valorFinal; }
    public void setValorFinal(Float valorFinal) { this.valorFinal = valorFinal; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getItensJson() { return itensJson; }
    public void setItensJson(String itensJson) { this.itensJson = itensJson; }
}
