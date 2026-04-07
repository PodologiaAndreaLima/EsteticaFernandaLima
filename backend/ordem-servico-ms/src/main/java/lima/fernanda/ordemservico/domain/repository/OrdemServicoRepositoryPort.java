package lima.fernanda.ordemservico.domain.repository;

import lima.fernanda.ordemservico.domain.model.OrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrdemServicoRepositoryPort {

    OrdemServico save(OrdemServico ordemServico);

    Optional<OrdemServico> findById(Integer id);

    Page<OrdemServico> findAll(Pageable pageable);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}

