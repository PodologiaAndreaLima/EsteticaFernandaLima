package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * dtHora, status, retorno e dataRetorno vêm do microsserviço de OS. São usados pela
 * jornada de lembretes: dataRetorno é a data prevista para a cliente voltar ("Precisa de
 * retorno?" na OS); retorno é a marcação antiga de OS que é ela própria um retorno.
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
        Boolean retorno,
        LocalDate dataRetorno
) {
    public OrdemServicoMsResponse(Integer id, Integer clienteId, Integer usuarioId, Float valorFinal,
                                  LocalDate dataCriacao, String observacao,
                                  List<OrdemServicoMsItemResponse> itens, LocalDateTime dtHora,
                                  String status, Boolean retorno) {
        this(id, clienteId, usuarioId, valorFinal, dataCriacao, observacao, itens, dtHora, status, retorno, null);
    }

    public OrdemServicoMsResponse(Integer id, Integer clienteId, Integer usuarioId, Float valorFinal,
                                  LocalDate dataCriacao, String observacao,
                                  List<OrdemServicoMsItemResponse> itens) {
        this(id, clienteId, usuarioId, valorFinal, dataCriacao, observacao, itens, null, null, null, null);
    }

    public boolean ehRetorno() {
        return Boolean.TRUE.equals(retorno);
    }

    public boolean statusIgual(String esperado) {
        return esperado.equalsIgnoreCase(status == null ? "PENDENTE" : status);
    }
}
