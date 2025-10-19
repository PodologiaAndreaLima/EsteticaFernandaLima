package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

public class VendaAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVendaAgendamento;
    private Float valorFinal;
    private LocalDate dtHora;
    private String observacao;

    public Integer getIdVendaAgendamento() {
        return idVendaAgendamento;
    }

    public void setIdVendaAgendamento(Integer idVendaAgendamento) {
        this.idVendaAgendamento = idVendaAgendamento;
    }

    public Float getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Float valorFinal) {
        this.valorFinal = valorFinal;
    }

    public LocalDate getDtHora() {
        return dtHora;
    }

    public void setDtHora(LocalDate dtHora) {
        this.dtHora = dtHora;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
