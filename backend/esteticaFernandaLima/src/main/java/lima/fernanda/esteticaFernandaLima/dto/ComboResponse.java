package lima.fernanda.esteticaFernandaLima.dto;

public record ComboResponse(
        Integer idCombo,
        String nome,
        Float valorFinal,
        String descricao
) {
    public static ComboResponse fromCombo(lima.fernanda.esteticaFernandaLima.model.Combo combo) {
        return new ComboResponse(
                combo.getIdCombo(),
                combo.getNome(),
                combo.getValorFinal(),
                combo.getDescricao()
        );
    }
}
