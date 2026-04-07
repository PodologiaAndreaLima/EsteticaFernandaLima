package lima.fernanda.ordemservico.application.dto;

import java.time.LocalDate;
import java.util.List;

public record OrdemServicoResponse(
        Integer id,
        Integer clienteId,
        Integer usuarioId,
        Float valorFinal,
        LocalDate dataCriacao,
        String observacao,
        List<ItemOrdemServicoResponse> itens
) {
}

