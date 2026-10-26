package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.NotaFiscalIdentidade;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalIdentidade.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface NotaFiscalIdentidadeRepository extends JpaRepository<NotaFiscalIdentidade, Long> {
    Optional<NotaFiscalIdentidade> findByUsuarioIdAndTipoIdentidadeAndIdentidadeDigest(
            Long usuarioId, Tipo tipo, String digest);

    /** Reserve while holding the own note lock; UNIQUE in the database decides concurrent collisions. */
    @Transactional(propagation = Propagation.MANDATORY)
    default NotaFiscalIdentidade reservar(NotaFiscalImportacaoRepository notas, Long notaId, Long usuarioId,
                                          Tipo tipo, String digest) {
        if (notaId == null || notaId <= 0 || usuarioId == null || usuarioId <= 0
                || tipo == null || digest == null || !digest.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Identidade invalida");
        }
        var nota = notas.bloquearPropria(notaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Nota nao encontrada"));
        if (nota.getStatus() == lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota.DESCARTADA) {
            throw new IllegalStateException("Nota descartada");
        }
        var identidade = new NotaFiscalIdentidade();
        identidade.setNota(nota);
        identidade.setUsuario(nota.getUsuario());
        identidade.setTipoIdentidade(tipo);
        identidade.setIdentidadeDigest(digest);
        return saveAndFlush(identidade);
    }

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from NotaFiscalIdentidade i where i.nota.id = :notaId and i.usuario.id = :usuarioId " +
            "and i.nota.status = 'DESCARTADA'")
    int liberarDescartada(@Param("notaId") Long notaId, @Param("usuarioId") Long usuarioId);
}
