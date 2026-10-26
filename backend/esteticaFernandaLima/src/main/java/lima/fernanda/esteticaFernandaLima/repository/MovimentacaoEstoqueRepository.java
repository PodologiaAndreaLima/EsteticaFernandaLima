package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    List<MovimentacaoEstoque> findByItemNotaIdAndItemNotaUsuarioIdOrderByItemOrdemAsc(Long notaId, Long usuarioId);

    boolean existsByItemIdAndItemNotaIdAndItemNotaUsuarioId(Long itemId, Long notaId, Long usuarioId);
}
