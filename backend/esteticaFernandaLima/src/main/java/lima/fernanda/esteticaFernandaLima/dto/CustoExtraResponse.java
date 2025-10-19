package lima.fernanda.esteticaFernandaLima.dto;

import java.time.LocalDate;

public record CustoExtraResponse(
        Integer idCustoExtra,
        String nome,
        String descricao,
        Float valor,
        LocalDate data
) {
    public static CustoExtraResponse fromCustoExtra(lima.fernanda.esteticaFernandaLima.model.CustoExtra custoExtra) {
        return new CustoExtraResponse(
                custoExtra.getIdCustoExtra(),
                custoExtra.getNome(),
                custoExtra.getDescricao(),
                custoExtra.getValor(),
                custoExtra.getData()
        );
    }
}
