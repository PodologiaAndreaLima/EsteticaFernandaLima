package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoProdutoRepository extends JpaRepository<ServicoProduto, Integer> {
    List<ServicoProduto> findByAtivo(Boolean ativo);
}