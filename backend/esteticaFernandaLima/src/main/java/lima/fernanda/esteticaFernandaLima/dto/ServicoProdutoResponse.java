package lima.fernanda.esteticaFernandaLima.dto;

public record ServicoProdutoResponse(
        Integer idProdutoServico,
        Boolean isProduto,
        String nome,
        Float despesa,
        Float valorVenda,
        String descricao,
        String marca,
        String categoria

) {
    public static ServicoProdutoResponse fromServicoProduto(lima.fernanda.esteticaFernandaLima.model.ServicoProduto servicoProduto) {
        return new ServicoProdutoResponse(
                servicoProduto.getIdProdutoServico(),
                servicoProduto.getProduto(),
                servicoProduto.getNome(),
                servicoProduto.getDespesa(),
                servicoProduto.getValorVenda(),
                servicoProduto.getDescricao(),
                servicoProduto.getMarca(),
                servicoProduto.getCategoria()
        );
    }
}
