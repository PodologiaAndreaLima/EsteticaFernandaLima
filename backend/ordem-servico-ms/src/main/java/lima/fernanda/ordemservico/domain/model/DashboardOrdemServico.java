package lima.fernanda.ordemservico.domain.model;

import java.time.LocalDate;

public class DashboardOrdemServico {

    private Integer id;
    private Integer ordemServicoId;
    private Integer clienteId;
    private Integer usuarioId;
    private Float valorFinal;
    private LocalDate dataCriacao;
    private String status;
    private Integer mes;
    private Integer ano;
    private String diaSemana;
    private String itensJson;

    public DashboardOrdemServico() {}

    public DashboardOrdemServico(Integer ordemServicoId, Integer clienteId, Integer usuarioId,
                                  Float valorFinal, LocalDate dataCriacao, String status,
                                  Integer mes, Integer ano, String diaSemana, String itensJson) {
        this.ordemServicoId = ordemServicoId;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.valorFinal = valorFinal;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.mes = mes;
        this.ano = ano;
        this.diaSemana = diaSemana;
        this.itensJson = itensJson;
    }

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
