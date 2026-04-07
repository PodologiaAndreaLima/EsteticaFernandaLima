package lima.fernanda.ordemservico.infrastructure.persistence.adapter;

import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoRepositoryPort;
import lima.fernanda.ordemservico.infrastructure.persistence.entity.OrdemServicoEntity;
import lima.fernanda.ordemservico.infrastructure.persistence.mapper.OrdemServicoEntityMapper;
import lima.fernanda.ordemservico.infrastructure.persistence.repository.SpringDataOrdemServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrdemServicoRepositoryAdapter implements OrdemServicoRepositoryPort {

    private final SpringDataOrdemServicoRepository repository;

    public OrdemServicoRepositoryAdapter(SpringDataOrdemServicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        OrdemServicoEntity saved = repository.save(OrdemServicoEntityMapper.toEntity(ordemServico));
        return OrdemServicoEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<OrdemServico> findById(Integer id) {
        return repository.findById(id).map(OrdemServicoEntityMapper::toDomain);
    }

    @Override
    public Page<OrdemServico> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(OrdemServicoEntityMapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return repository.existsById(id);
    }
}

