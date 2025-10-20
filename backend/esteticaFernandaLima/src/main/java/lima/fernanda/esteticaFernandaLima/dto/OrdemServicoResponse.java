package lima.fernanda.esteticaFernandaLima.dto;

import java.time.LocalDate;

public record OrdemServicoResponse(
        Integer idOrdemServico,
        Float valorFinal,
        LocalDate dtHora,
        String observacao
) {
    public static OrdemServicoResponse fromOrdemServico(lima.fernanda.esteticaFernandaLima.model.OrdemServico ordemServico) {
        return new OrdemServicoResponse(
                ordemServico.getIdOrdemServico(),
                ordemServico.getValorFinal(),
                ordemServico.getDtHora(),
                ordemServico.getObservacao()
        );
    }
}
