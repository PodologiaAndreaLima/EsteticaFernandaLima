package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;

public record ItemComboResponse(
        Integer idProdutoServico,
        String nome,
        Float valorVenda,
        Boolean ativo
) {
    public static ItemComboResponse from(ServicoProduto servicoProduto) {
        return new ItemComboResponse(
                servicoProduto.getIdProdutoServico(),
                servicoProduto.getNome(),
                servicoProduto.getValorVenda(),
                servicoProduto.getAtivo()
        );
    }
}