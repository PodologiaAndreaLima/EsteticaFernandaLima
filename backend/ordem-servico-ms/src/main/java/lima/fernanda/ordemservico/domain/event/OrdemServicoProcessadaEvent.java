package lima.fernanda.ordemservico.domain.event;

import java.time.LocalDate;

public class OrdemServicoProcessadaEvent {
    private Integer ordemServicoId;
    private String status;
    private LocalDate dataProcessamento;

    public OrdemServicoProcessadaEvent(Integer ordemServicoId, String status, LocalDate dataProcessamento) {
        this.ordemServicoId = ordemServicoId;
        this.status = status;
        this.dataProcessamento = dataProcessamento;
    }

    public Integer getOrdemServicoId() {
        return ordemServicoId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDataProcessamento() {
        return dataProcessamento;
    }
}
