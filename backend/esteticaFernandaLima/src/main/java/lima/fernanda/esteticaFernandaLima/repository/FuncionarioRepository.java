package lima.fernanda.esteticaFernandaLima.repository;

import java.util.List;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    @Query("SELECT f FROM Funcionario f WHERE f.CPF = :cpf")
    List<Funcionario> findAllByCpf(@Param("cpf") String cpf);

    List<Funcionario> findAllByEmailIgnoreCase(String email);
}
