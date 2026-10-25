package lima.fernanda.ordemservico.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;
import java.time.LocalDateTime;

public record StatusOrdemServicoRequest(
        @NotNull StatusOrdemServico status,
        @JsonProperty("dt_hora") LocalDateTime dtHora
) {}
