package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.NotaFiscalItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaFiscalItemRepository extends JpaRepository<NotaFiscalItem, Long> {
    List<NotaFiscalItem> findByNotaIdAndNotaUsuarioIdOrderByOrdemAsc(Long notaId, Long usuarioId);

    Optional<NotaFiscalItem> findByIdAndNotaIdAndNotaUsuarioId(Long id, Long notaId, Long usuarioId);
}
