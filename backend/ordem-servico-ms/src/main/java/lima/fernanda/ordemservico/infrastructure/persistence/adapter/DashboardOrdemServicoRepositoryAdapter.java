package lima.fernanda.ordemservico.infrastructure.persistence.adapter;

import jakarta.transaction.Transactional;
import lima.fernanda.ordemservico.domain.model.DashboardOrdemServico;
import lima.fernanda.ordemservico.domain.repository.DashboardOrdemServicoRepositoryPort;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.DashboardOrdemServicoEntity;
import lima.fernanda.ordemservico.infrastructure.persistence.repository.SpringDataDashboardOrdemServicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class DashboardOrdemServicoRepositoryAdapter implements DashboardOrdemServicoRepositoryPort {

    private final SpringDataDashboardOrdemServicoRepository repository;

    public DashboardOrdemServicoRepositoryAdapter(SpringDataDashboardOrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public DashboardOrdemServico save(DashboardOrdemServico dashboard) {
        DashboardOrdemServicoEntity entity = toEntity(dashboard);
        DashboardOrdemServicoEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<DashboardOrdemServico> findByOrdemServicoId(Integer ordemServicoId) {
        return repository.findByOrdemServicoId(ordemServicoId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByOrdemServicoId(Integer ordemServicoId) {
        repository.deleteByOrdemServicoId(ordemServicoId);
    }

    @Override
    public List<DashboardOrdemServico> findByMesAndAno(int mes, int ano) {
        return repository.findByMesAndAno(mes, ano).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardOrdemServico> findByAno(int ano) {
        return repository.findByAno(ano).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardOrdemServico> findByMesAndAnoAndUsuarioId(int mes, int ano, Integer usuarioId) {
        return repository.findByMesAndAnoAndUsuarioId(mes, ano, usuarioId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ── Mapeamentos ────────────────────────────────────────────────────────

    private DashboardOrdemServicoEntity toEntity(DashboardOrdemServico d) {
        DashboardOrdemServicoEntity e = new DashboardOrdemServicoEntity();
        e.setId(d.getId());
        e.setOrdemServicoId(d.getOrdemServicoId());
        e.setClienteId(d.getClienteId());
        e.setUsuarioId(d.getUsuarioId());
        e.setValorFinal(d.getValorFinal());
        e.setDataCriacao(d.getDataCriacao());
        e.setStatus(d.getStatus());
        e.setMes(d.getMes());
        e.setAno(d.getAno());
        e.setDiaSemana(d.getDiaSemana());
        e.setItensJson(d.getItensJson());
        return e;
    }

    private DashboardOrdemServico toDomain(DashboardOrdemServicoEntity e) {
        DashboardOrdemServico d = new DashboardOrdemServico();
        d.setId(e.getId());
        d.setOrdemServicoId(e.getOrdemServicoId());
        d.setClienteId(e.getClienteId());
        d.setUsuarioId(e.getUsuarioId());
        d.setValorFinal(e.getValorFinal());
        d.setDataCriacao(e.getDataCriacao());
        d.setStatus(e.getStatus());
        d.setMes(e.getMes());
        d.setAno(e.getAno());
        d.setDiaSemana(e.getDiaSemana());
        d.setItensJson(e.getItensJson());
        return d;
    }
}
