package lima.fernanda.ordemservico.infrastructure.persistence.repository;

import lima.fernanda.ordemservico.infrastructure.persistence.entity.OrdemServicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataOrdemServicoRepository extends JpaRepository<OrdemServicoEntity, Integer> {

    @Override
    @EntityGraph(attributePaths = "itens")
    Page<OrdemServicoEntity> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "itens")
    Optional<OrdemServicoEntity> findById(Integer id);
}
