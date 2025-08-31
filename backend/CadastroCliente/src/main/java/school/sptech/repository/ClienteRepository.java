package school.sptech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Cliente;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByNomeCompletoContainingIgnoreCase(String nome);


}
