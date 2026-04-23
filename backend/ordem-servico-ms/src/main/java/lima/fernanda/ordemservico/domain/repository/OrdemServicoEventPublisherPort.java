package lima.fernanda.ordemservico.domain.repository;

import lima.fernanda.ordemservico.domain.model.OrdemServico;

public interface OrdemServicoEventPublisherPort {

    void publicarOrdemCriada(OrdemServico ordemServico);

    void publicarOrdemAtualizada(OrdemServico ordemServico);

    void publicarOrdemDeletada(Integer ordemServicoId);
}
