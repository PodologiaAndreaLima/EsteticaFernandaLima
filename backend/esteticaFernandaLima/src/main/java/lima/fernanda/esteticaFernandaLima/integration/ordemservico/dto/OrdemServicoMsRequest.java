package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;

import java.util.List;

public record OrdemServicoMsRequest(
        Integer clienteId,
        Integer usuarioId,
        Float valorFinal,
        String observacao,
        List<OrdemServicoMsItemRequest> itens
) {
}
