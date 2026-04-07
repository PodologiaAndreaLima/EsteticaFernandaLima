package lima.fernanda.ordemservico.application.dto;

public record ItemOrdemServicoRequest(
        Integer servicoProdutoId,
        Integer comboId,
        Integer produtoId,
        Integer quantidade,
        Float desconto
) {
}

