package lima.fernanda.esteticaFernandaLima.dto;

public record CustoFixoResponse(
        Integer idCustoFixo,
        String nome,
        String descricao,
        Float valorMensal
) {
    public static CustoFixoResponse fromCustoFixo(lima.fernanda.esteticaFernandaLima.model.CustoFixo custoFixo) {
        return new CustoFixoResponse(
                custoFixo.getIdCustoFixo(),
                custoFixo.getNome(),
                custoFixo.getDescricao(),
                custoFixo.getValorMensal()
        );
    }
}
