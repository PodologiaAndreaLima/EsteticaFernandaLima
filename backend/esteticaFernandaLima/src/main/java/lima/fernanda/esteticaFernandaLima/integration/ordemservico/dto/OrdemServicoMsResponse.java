package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;

import java.time.LocalDate;
import java.util.List;

public record OrdemServicoMsResponse(
        Integer id,
        Integer clienteId,
        Integer usuarioId,
        Float valorFinal,
        LocalDate dataCriacao,
        String observacao,
        List<OrdemServicoMsItemResponse> itens
) {
}

