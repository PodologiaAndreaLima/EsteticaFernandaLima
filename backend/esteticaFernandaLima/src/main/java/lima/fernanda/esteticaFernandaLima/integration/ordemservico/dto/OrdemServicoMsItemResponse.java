package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;

public record OrdemServicoMsItemResponse(
        Integer servicoProdutoId,
        Integer comboId,
        Integer produtoId,
        Integer quantidade,
        Float desconto
) {
}

