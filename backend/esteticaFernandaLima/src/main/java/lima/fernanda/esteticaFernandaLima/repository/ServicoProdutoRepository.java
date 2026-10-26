package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface ServicoProdutoRepository extends JpaRepository<ServicoProduto, Integer> {
    List<ServicoProduto> findByAtivo(Boolean ativo);

    Optional<ServicoProduto> findByIdProdutoServicoAndUsuarioIdAndProdutoTrueAndAtivoTrue(Integer id, Long usuarioId);

    List<ServicoProduto> findByUsuarioIdAndProdutoTrueAndAtivoTrueOrderByIdProdutoServicoAsc(Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ServicoProduto p where p.idProdutoServico = :id and p.usuario.id = :usuarioId and p.produto = true and p.ativo = true")
    Optional<ServicoProduto> bloquearProdutoProprio(@Param("id") Integer id, @Param("usuarioId") Long usuarioId);

    /** Call inside one transaction; acquire locks in ascending ID order before any updates. */
    @Transactional(propagation = Propagation.MANDATORY)
    default List<ServicoProduto> bloquearProdutosOrdenados(List<Integer> ids, Long usuarioId) {
        if (usuarioId == null || ids == null || ids.isEmpty() || ids.stream().anyMatch(id -> id == null || id <= 0)
                || ids.size() != ids.stream().distinct().count()) {
            throw new IllegalArgumentException("IDs de produtos ou dono invalidos");
        }
        return ids.stream().sorted().map(id -> bloquearProdutoProprio(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao elegivel"))).toList();
    }

    /** Caller must hold the product row lock; check exactly one affected row and refresh before reading stock. */
    @Transactional(propagation = Propagation.MANDATORY)
    default int incrementarEstoqueECusto(Integer id, Long usuarioId, Integer quantidade, BigDecimal custo) {
        if (id == null || id <= 0 || usuarioId == null || usuarioId <= 0 || quantidade == null || quantidade <= 0
                || custo == null || custo.scale() > 2 || custo.compareTo(new BigDecimal("0.01")) < 0
                || custo.compareTo(new BigDecimal("9999999.99")) > 0) {
            throw new IllegalArgumentException("Incremento ou custo invalido");
        }
        if (bloquearProdutoProprio(id, usuarioId).isEmpty()) {
            return 0;
        }
        return aplicarIncrementoEstoqueECusto(id, usuarioId, quantidade, custo.floatValue());
    }

    @Modifying(flushAutomatically = true)
    @Query("update ServicoProduto p set p.estoque = p.estoque + :quantidade, p.despesa = :custo " +
            "where p.idProdutoServico = :id and p.usuario.id = :usuarioId and p.produto = true and p.ativo = true " +
            "and p.estoque >= 0 and p.estoque <= 2147483647 - :quantidade and :quantidade > 0 " +
            "and :custo >= 0.01 and :custo <= 10000000")
    int aplicarIncrementoEstoqueECusto(@Param("id") Integer id, @Param("usuarioId") Long usuarioId,
                                 @Param("quantidade") Integer quantidade, @Param("custo") Float custo);
}
