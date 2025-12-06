package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustoFixoRepository extends JpaRepository<CustoFixo, Integer> {
    @Query("SELECT c.valorMensal FROM CustoFixo c")
    List<Float> findAllValoresMensais();
}
