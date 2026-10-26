package lima.fernanda.ordemservico.application.dto;

import jakarta.validation.constraints.*;

public record ItemOrdemServicoRequest(
        @Positive Integer servicoProdutoId,
        @Positive Integer comboId,
        @Positive Integer produtoId,
        @Positive Integer quantidade,
        @DecimalMin("0") @DecimalMax("100") Float desconto,
        @Size(max = 200) String nomeServicoProduto,
        @Size(max = 200) String nomeCombo,
        Boolean ehProduto,
        // Preço unitário do catálogo no momento em que o item entrou na OS
        @PositiveOrZero Float valorUnitario
) {
    /** Mantém compatibilidade com quem ainda não envia valorUnitario. */
    public ItemOrdemServicoRequest(Integer servicoProdutoId, Integer comboId, Integer produtoId,
                                   Integer quantidade, Float desconto, String nomeServicoProduto,
                                   String nomeCombo, Boolean ehProduto) {
        this(servicoProdutoId, comboId, produtoId, quantidade, desconto, nomeServicoProduto, nomeCombo, ehProduto, null);
    }

    @AssertTrue(message = "Informe apenas uma referência de serviço, produto ou combo")
    public boolean isReferenciaValida() {
        return (servicoProdutoId == null ? 0 : 1) + (comboId == null ? 0 : 1) + (produtoId == null ? 0 : 1) == 1;
    }
}
