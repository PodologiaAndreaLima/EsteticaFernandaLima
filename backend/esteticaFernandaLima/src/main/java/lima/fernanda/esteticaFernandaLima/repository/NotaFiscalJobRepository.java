package lima.fernanda.esteticaFernandaLima.repository;

import lima.fernanda.esteticaFernandaLima.model.NotaFiscalJob;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface NotaFiscalJobRepository extends JpaRepository<NotaFiscalJob, Long> {
    @Query("select j.notaId from NotaFiscalJob j where j.nota.status = 'PROCESSANDO' " +
            "and j.tentativas < :limite and (j.status = 'PENDENTE' " +
            "or (j.status = 'EM_EXECUCAO' and j.leaseAte < :agora)) order by j.criadoEm")
    List<Long> candidatos(@Param("agora") Instant agora, @Param("limite") int limite, Pageable pagina);

    @Query("select j.notaId from NotaFiscalJob j where j.nota.status = 'PROCESSANDO' " +
            "and j.tentativas >= :limite and j.status = 'EM_EXECUCAO' and j.leaseAte < :agora order by j.criadoEm")
    List<Long> esgotados(@Param("agora") Instant agora, @Param("limite") int limite, Pageable pagina);

    @Modifying
    @Query("update NotaFiscalJob j set j.status = 'EM_EXECUCAO', j.tentativas = j.tentativas + 1, " +
            "j.leaseAte = :ate, j.versao = j.versao + 1 where j.notaId = :id " +
            "and j.nota.status = 'PROCESSANDO' and j.tentativas < :limite " +
            "and (j.status = 'PENDENTE' or (j.status = 'EM_EXECUCAO' and j.leaseAte < :agora))")
    int adquirir(@Param("id") Long id, @Param("agora") Instant agora, @Param("ate") Instant ate,
                 @Param("limite") int limite);

    @Modifying
    @Query("update NotaFiscalJob j set j.status = :estado, j.leaseAte = null, j.versao = j.versao + 1 " +
            "where j.notaId = :id and j.status = 'EM_EXECUCAO' and j.leaseAte = :ate")
    int encerrar(@Param("id") Long id, @Param("ate") Instant ate, @Param("estado") NotaFiscalJob.Estado estado);

    @Modifying
    @Query("update NotaFiscalJob j set j.status = 'CONCLUIDO', j.leaseAte = null, j.versao = j.versao + 1 " +
            "where j.notaId = :id and j.status = 'EM_EXECUCAO' and j.tentativas >= :limite " +
            "and j.leaseAte < :agora and j.leaseAte = :ate")
    int encerrarEsgotado(@Param("id") Long id, @Param("ate") Instant ate,
                         @Param("agora") Instant agora, @Param("limite") int limite);
}
