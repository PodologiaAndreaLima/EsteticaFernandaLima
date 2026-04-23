package lima.fernanda.ordemservico.domain.repository;

import lima.fernanda.ordemservico.domain.model.DashboardOrdemServico;

import java.util.List;
import java.util.Optional;


public interface DashboardOrdemServicoRepositoryPort {

    DashboardOrdemServico save(DashboardOrdemServico dashboard);

    Optional<DashboardOrdemServico> findByOrdemServicoId(Integer ordemServicoId);

    void deleteByOrdemServicoId(Integer ordemServicoId);

    /** Retorna todas as ordens de um mês/ano específico */
    List<DashboardOrdemServico> findByMesAndAno(int mes, int ano);

    /** Retorna todas as ordens de um ano (para gráfico anual) */
    List<DashboardOrdemServico> findByAno(int ano);

    /** Retorna ordens de um mês/ano filtradas por usuário */
    List<DashboardOrdemServico> findByMesAndAnoAndUsuarioId(int mes, int ano, Integer usuarioId);
}
