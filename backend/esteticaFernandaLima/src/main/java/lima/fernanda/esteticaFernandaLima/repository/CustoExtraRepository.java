package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.beans.JavaBean;
import java.time.LocalDate;
import java.util.List;

public interface CustoExtraRepository extends JpaRepository<CustoExtra, Integer> {

    @Query("SELECT c.valor FROM CustoExtra c WHERE MONTH(c.data) = MONTH(:dataAtual) AND YEAR(c.data) = YEAR(:dataAtual)")
    List<Float> findValoresByMesAtual(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT c.valor FROM CustoExtra c WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes")
    List<Float> findValoresByAnoEMes(@Param("ano") int ano, @Param("mes") int mes);
}
