package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.model.ComboServicoProduto;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;

import java.util.List;

public record ComboDetalheResponse(
        Integer idCombo,
        String nome,
        String descricao,
        Float valorFinal,
        Float desconto,
        Boolean ativo,
        List<ItemComboResponse> servicos,
        List<ItemComboResponse> produtos
) {
    public static ComboDetalheResponse fromCombo(Combo combo) {
        List<ServicoProduto> itens = combo.getItensCombo().stream()
                .map(ComboServicoProduto::getServicoProduto)
                .toList();

        return new ComboDetalheResponse(
                combo.getIdCombo(),
                combo.getNome(),
                combo.getDescricao(),
                combo.getValorFinal(),
                combo.getDesconto() != null ? combo.getDesconto() : 0f,
                combo.getAtivo(),
                itens.stream().filter(i -> !Boolean.TRUE.equals(i.getProduto())).map(ItemComboResponse::from).toList(),
                itens.stream().filter(i -> Boolean.TRUE.equals(i.getProduto())).map(ItemComboResponse::from).toList()
        );
    }
}