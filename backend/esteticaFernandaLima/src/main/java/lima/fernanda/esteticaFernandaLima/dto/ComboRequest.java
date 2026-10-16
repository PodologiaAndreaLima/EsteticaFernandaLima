package lima.fernanda.esteticaFernandaLima.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record ComboRequest(
        @NotBlank(message = "Informe o nome do combo") String nome,
        String descricao,
        @NotNull(message = "Informe o valor final") @PositiveOrZero Float valorFinal,
        @DecimalMin("0.0") @DecimalMax("100.0") Float desconto,
        List<Integer> idsServicos,   // ausente (web) = não altera os itens
        List<Integer> idsProdutos
) {
}