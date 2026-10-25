package lima.fernanda.ordemservico.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;
import java.time.LocalDateTime;

import java.util.List;

public record OrdemServicoRequest(
        @NotNull @Positive Integer clienteId,
        @NotNull @Positive Integer usuarioId,
        @NotNull @PositiveOrZero Float valorFinal,
        @Size(max = 255) String observacao,
        @Valid List<@NotNull ItemOrdemServicoRequest> itens,
        @NotNull @JsonProperty("dt_hora") LocalDateTime dtHora,
        StatusOrdemServico status,
        Boolean retorno
) {
}

