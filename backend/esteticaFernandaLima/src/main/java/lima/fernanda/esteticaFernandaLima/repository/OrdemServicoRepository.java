package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    @Query("SELECT o.valorFinal FROM OrdemServico o WHERE MONTH(o.dtHora) = MONTH(:dataAtual) AND YEAR(o.dtHora) = YEAR(:dataAtual)")
    List<Float> findValoresFinaisByMesAtual(@Param("dataAtual") LocalDate dataAtual);

    @Query("SELECT o.valorFinal FROM OrdemServico o WHERE YEAR(o.dtHora) = :ano AND MONTH(o.dtHora) = :mes")
    List<Float> findValoresFinaisByAnoEMes(@Param("ano") int ano, @Param("mes") int mes);

    @Query("SELECT COUNT(o) FROM OrdemServico o WHERE MONTH(o.dtHora) = MONTH(:dataAtual) AND YEAR(o.dtHora) = YEAR(:dataAtual)")
    Long countOrdensServicoByMesAtual(@Param("dataAtual") LocalDate dataAtual);

    @Query("""
            SELECT new lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO(
                COALESCE(c.nome, sp.nome),
                SUM(vps.quantidade)
            )
            FROM OrdemServico os
            JOIN os.itens vps
            LEFT JOIN vps.combo c
            JOIN vps.servicoProduto sp
            WHERE (vps.combo IS NOT NULL OR sp.produto = false)
              AND os.dtHora BETWEEN :inicio AND :fim
            GROUP BY COALESCE(c.nome, sp.nome)
            ORDER BY SUM(vps.quantidade) DESC
            """)
    List<ServicoQuantidadeDTO> buscarMaisVendidos(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            Pageable pageable);

    @Query("""
            SELECT new lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO(
                sp.nome,
                SUM(vps.quantidade)
            )
            FROM OrdemServico os
            JOIN os.itens vps
            JOIN vps.servicoProduto sp
            WHERE os.dtHora BETWEEN :inicio AND :fim
                      AND sp.produto = true
            GROUP BY sp.id, sp.nome
            ORDER BY SUM(vps.quantidade) DESC
            """)
    List<ProdutoQuantidadeDTO> buscarProdutosMaisVendidos(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            Pageable pageable);

    @Query("""
            SELECT SUM(o.valorFinal)
            FROM OrdemServico o
            WHERE MONTH(o.dtHora) = MONTH(:dataAtual)
              AND YEAR(o.dtHora) = YEAR(:dataAtual)
              AND o.usuario.id = 2
            """)
    Float getReceitaTotalFuncionarioMesAtual(@Param("dataAtual") LocalDate dataAtual);

    @Query("""
        SELECT COUNT(o)
        FROM OrdemServico o
        WHERE MONTH(o.dtHora) = MONTH(:dataAtual)
          AND YEAR(o.dtHora) = YEAR(:dataAtual)
          AND o.usuario.id = 2
        """)
    Long getQuantidadeOrdensFuncionarioMesAtual(@Param("dataAtual") LocalDate dataAtual);
}