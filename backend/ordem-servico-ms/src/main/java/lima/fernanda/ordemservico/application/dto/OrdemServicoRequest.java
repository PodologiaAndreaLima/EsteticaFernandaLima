package lima.fernanda.ordemservico.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrdemServicoRequest(
        @NotNull Integer clienteId,
        @NotNull Integer usuarioId,
        @NotNull Float valorFinal,
        String observacao,
        @Valid List<ItemOrdemServicoRequest> itens
) {
}

