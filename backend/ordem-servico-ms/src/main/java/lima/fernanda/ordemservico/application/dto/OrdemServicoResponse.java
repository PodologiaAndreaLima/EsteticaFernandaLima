package lima.fernanda.ordemservico.application.dto;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;

public record OrdemServicoResponse(
        Integer id,
        Integer clienteId,
        Integer usuarioId,
        Float valorFinal,
        LocalDate dataCriacao,
        String observacao,
        List<ItemOrdemServicoResponse> itens,
        @JsonProperty("dt_hora") LocalDateTime dtHora,
        StatusOrdemServico status,
        boolean retorno
) {
}

