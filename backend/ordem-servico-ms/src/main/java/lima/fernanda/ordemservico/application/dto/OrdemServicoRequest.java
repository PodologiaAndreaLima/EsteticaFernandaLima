package lima.fernanda.ordemservico.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;
import java.time.LocalDate;
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
        Boolean retorno,
        // Data prevista do retorno da cliente (opcional). Deve ser posterior ao atendimento.
        LocalDate dataRetorno
) {
    /** Mantém compatibilidade com quem ainda não envia dataRetorno. */
    public OrdemServicoRequest(Integer clienteId, Integer usuarioId, Float valorFinal, String observacao,
                               List<ItemOrdemServicoRequest> itens, LocalDateTime dtHora,
                               StatusOrdemServico status, Boolean retorno) {
        this(clienteId, usuarioId, valorFinal, observacao, itens, dtHora, status, retorno, null);
    }
}

