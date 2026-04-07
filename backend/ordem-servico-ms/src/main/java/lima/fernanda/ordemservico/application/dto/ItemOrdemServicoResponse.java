package lima.fernanda.ordemservico.application.dto;

public record ItemOrdemServicoResponse(
        Integer servicoProdutoId,
        Integer comboId,
        Integer produtoId,
        Integer quantidade,
        Float desconto
) {
}

