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
        Boolean ehProduto
) {
    @AssertTrue(message = "Informe apenas uma referência de serviço, produto ou combo")
    public boolean isReferenciaValida() {
        return (servicoProdutoId == null ? 0 : 1) + (comboId == null ? 0 : 1) + (produtoId == null ? 0 : 1) == 1;
    }
}
