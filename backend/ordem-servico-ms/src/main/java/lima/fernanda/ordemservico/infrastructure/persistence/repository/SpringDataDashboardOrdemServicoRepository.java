package lima.fernanda.ordemservico.infrastructure.persistence.repository;

import lima.fernanda.ordemservico.infrastructure.persistence.entity.DashboardOrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataDashboardOrdemServicoRepository
        extends JpaRepository<DashboardOrdemServicoEntity, Integer> {

    Optional<DashboardOrdemServicoEntity> findByOrdemServicoId(Integer ordemServicoId);

    void deleteByOrdemServicoId(Integer ordemServicoId);

    List<DashboardOrdemServicoEntity> findByMesAndAno(int mes, int ano);

    List<DashboardOrdemServicoEntity> findByAno(int ano);

    List<DashboardOrdemServicoEntity> findByMesAndAnoAndUsuarioId(int mes, int ano, Integer usuarioId);
}
