package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * dtHora, status e retorno vêm do microsserviço de OS (atendimento, situação e
 * se a OS é um retorno). São usados pela jornada de lembretes.
 */
public record OrdemServicoMsResponse(
        Integer id,
        Integer clienteId,
        Integer usuarioId,
        Float valorFinal,
        LocalDate dataCriacao,
        String observacao,
        List<OrdemServicoMsItemResponse> itens,
        @JsonProperty("dt_hora") LocalDateTime dtHora,
        String status,
        Boolean retorno
) {
    public OrdemServicoMsResponse(Integer id, Integer clienteId, Integer usuarioId, Float valorFinal,
                                  LocalDate dataCriacao, String observacao,
                                  List<OrdemServicoMsItemResponse> itens) {
        this(id, clienteId, usuarioId, valorFinal, dataCriacao, observacao, itens, null, null, null);
    }

    public boolean ehRetorno() {
        return Boolean.TRUE.equals(retorno);
    }

    public boolean statusIgual(String esperado) {
        return esperado.equalsIgnoreCase(status == null ? "PENDENTE" : status);
    }
}
