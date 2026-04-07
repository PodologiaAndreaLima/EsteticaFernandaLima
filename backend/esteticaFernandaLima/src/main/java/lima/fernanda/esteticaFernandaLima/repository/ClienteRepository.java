package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNomeCompletoContainingIgnoreCase(String nome);

    Page<Cliente> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);
}