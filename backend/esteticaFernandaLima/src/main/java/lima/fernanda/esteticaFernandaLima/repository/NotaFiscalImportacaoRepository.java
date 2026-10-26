package lima.fernanda.esteticaFernandaLima.repository;

import jakarta.persistence.LockModeType;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalImportacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface NotaFiscalImportacaoRepository extends JpaRepository<NotaFiscalImportacao, Long> {
    @Query("select n.id from NotaFiscalImportacao n where n.status in ('CONFIRMADA','FALHA','DESCARTADA') " +
            "and n.estadoLimpeza in ('PENDENTE','FALHOU') and n.limpezaTentativas < 3 " +
            "and (n.estadoLimpeza = 'PENDENTE' or n.limpezaEm < :antes) order by n.id")
    List<Long> pendentesLimpeza(@Param("antes") Instant antes, Pageable pagina);

    @Query("select count(n) from NotaFiscalImportacao n where n.status in ('CONFIRMADA','FALHA','DESCARTADA') " +
            "and n.estadoLimpeza = 'FALHOU' and n.imagemChave is not null and n.limpezaTentativas >= 3")
    long contarLimpezasEsgotadas();
    Optional<NotaFiscalImportacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NotaFiscalImportacao n where n.id = :id and n.usuario.id = :usuarioId")
    Optional<NotaFiscalImportacao> bloquearPropria(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NotaFiscalImportacao n set n.status = :destino, n.versao = n.versao + 1, " +
            "n.finalizadaEm = :instante, n.estadoLimpeza = 'PENDENTE', n.codigoFalha = :falha " +
            "where n.id = :id and n.usuario.id = :usuarioId and n.status in :origens and n.versao = :versao " +
            "and ((:destino = 'DESCARTADA' and n.status in ('PROCESSANDO','EM_REVISAO') and :falha is null) " +
            "or (:destino = 'CONFIRMADA' and n.status = 'EM_REVISAO' and :falha is null) " +
            "or (:destino = 'FALHA' and n.status in ('PROCESSANDO','EM_REVISAO') and :falha is not null)) " +
            "and n.imagemChave is not null and n.estadoLimpeza = 'DISPONIVEL'")
    int finalizar(@Param("id") Long id, @Param("usuarioId") Long usuarioId,
                  @Param("versao") Long versao, @Param("origens") List<EstadoNota> origens,
                  @Param("destino") EstadoNota destino, @Param("falha") CodigoFalha falha,
                  @Param("instante") Instant instante);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NotaFiscalImportacao n set n.status = 'EM_REVISAO', n.versao = n.versao + 1 " +
            "where n.id = :id and n.usuario.id = :usuarioId and n.status = 'PROCESSANDO' and n.versao = :versao")
    int publicarRevisao(@Param("id") Long id, @Param("usuarioId") Long usuarioId, @Param("versao") Long versao);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NotaFiscalImportacao n set n.imagemChave = null, n.estadoLimpeza = 'EXCLUIDA', " +
            "n.limpezaEm = :instante, n.limpezaCodigo = null, n.limpezaTentativas = n.limpezaTentativas + 1, " +
            "n.versao = n.versao + 1 where n.id = :id and n.usuario.id = :usuarioId " +
            "and n.status in ('CONFIRMADA','FALHA','DESCARTADA') and n.estadoLimpeza in ('PENDENTE','FALHOU') " +
            "and n.imagemChave is not null and n.limpezaTentativas < 3 and n.versao = :versao")
    int registrarImagemExcluida(@Param("id") Long id, @Param("usuarioId") Long usuarioId,
                                @Param("versao") Long versao, @Param("instante") Instant instante);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update NotaFiscalImportacao n set n.estadoLimpeza = 'FALHOU', n.limpezaCodigo = :codigo, " +
            "n.limpezaEm = :instante, n.limpezaTentativas = n.limpezaTentativas + 1, n.versao = n.versao + 1 " +
            "where n.id = :id and n.usuario.id = :usuarioId and n.status in ('CONFIRMADA','FALHA','DESCARTADA') " +
            "and n.estadoLimpeza in ('PENDENTE','FALHOU') and n.imagemChave is not null " +
            "and n.limpezaTentativas < 3 and n.versao = :versao")
    int registrarFalhaLimpeza(@Param("id") Long id, @Param("usuarioId") Long usuarioId,
                              @Param("versao") Long versao, @Param("codigo") String codigo,
                              @Param("instante") Instant instante);
}
