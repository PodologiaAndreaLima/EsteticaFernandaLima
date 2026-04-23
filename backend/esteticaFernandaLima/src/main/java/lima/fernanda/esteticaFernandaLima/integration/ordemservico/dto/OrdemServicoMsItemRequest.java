package lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto;


public record OrdemServicoMsItemRequest(
        Integer servicoProdutoId,
        Integer comboId,
        Integer produtoId,
        Integer quantidade,
        Float desconto,
        String nomeServicoProduto,
        String nomeCombo,
        Boolean ehProduto
) {
}
