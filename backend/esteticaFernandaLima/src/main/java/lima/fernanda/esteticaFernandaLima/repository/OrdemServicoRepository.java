package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    @Query("SELECT o.valorFinal FROM OrdemServico o WHERE MONTH(o.dtHora) = MONTH(:dataAtual) AND YEAR(o.dtHora) = YEAR(:dataAtual)")
    List<Float> findValoresFinaisByMesAtual(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT o.valorFinal FROM OrdemServico o WHERE YEAR(o.dtHora) = :ano AND MONTH(o.dtHora) = :mes")
    List<Float> findValoresFinaisByAnoEMes(@Param("ano") int ano, @Param("mes") int mes);

    @Query("SELECT COUNT(o) FROM OrdemServico o WHERE MONTH(o.dtHora) = MONTH(:dataAtual) AND YEAR(o.dtHora) = YEAR(:dataAtual)")
    Long countOrdensServicoByMesAtual(@Param("dataAtual") LocalDate dataAtual);
}
