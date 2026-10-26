package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.model.*;
import lima.fernanda.esteticaFernandaLima.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReservaNota {
    private final NotaFiscalImportacaoRepository notas;
    private final NotaFiscalIdentidadeRepository identidades;
    private final NotaFiscalJobRepository jobs;
    private final UsuarioRepository usuarios;

    public ReservaNota(NotaFiscalImportacaoRepository notas, NotaFiscalIdentidadeRepository identidades,
                       NotaFiscalJobRepository jobs, UsuarioRepository usuarios) {
        this.notas = notas;
        this.identidades = identidades;
        this.jobs = jobs;
        this.usuarios = usuarios;
    }

    @Transactional
    public Long reservar(Long usuarioId, ArmazenamentoImagemNota.Imagem imagem) {
        var nota = new NotaFiscalImportacao();
        nota.setUsuario(usuarios.getReferenceById(usuarioId));
        nota.setImagemChave(imagem.chave());
        nota.setImagemDigest(imagem.digest());
        nota.setCriadaEm(Instant.now());
        notas.saveAndFlush(nota);
        identidades.reservar(notas, nota.getId(), usuarioId, NotaFiscalIdentidade.Tipo.SOMENTE_IMAGEM,
                imagem.digest());
        var job = new NotaFiscalJob();
        job.setNota(nota);
        job.setCriadoEm(Instant.now());
        jobs.saveAndFlush(job);
        return nota.getId();
    }
}
