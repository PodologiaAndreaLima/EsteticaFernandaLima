package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalImportacaoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LimpezaImagemNota {
    private static final Logger LOG = LoggerFactory.getLogger(LimpezaImagemNota.class);
    private final NotaFiscalImportacaoRepository notas;
    private final ArmazenamentoImagemNota armazenamento;
    private final TransactionTemplate transacao;

    public LimpezaImagemNota(NotaFiscalImportacaoRepository notas, ArmazenamentoImagemNota armazenamento,
                             PlatformTransactionManager manager) {
        this.notas = notas;
        this.armazenamento = armazenamento;
        this.transacao = new TransactionTemplate(manager);
        this.transacao.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Scheduled(fixedDelay = 30000)
    public void executar() {
        for (Long id : notas.pendentesLimpeza(Instant.now().minusSeconds(60), PageRequest.of(0, 20))) limpar(id);
    }

    @Scheduled(fixedDelay = 300000)
    public void alertarLimpezasEsgotadas() {
        long quantidade = notas.contarLimpezasEsgotadas();
        if (quantidade > 0) LOG.error("Limpeza de imagens esgotada: {} notas exigem intervencao operacional", quantidade);
    }

    public void limpar(Long id) {
        var nota = notas.findById(id).orElse(null);
        if (nota == null || nota.getImagemChave() == null) return;
        try {
            armazenamento.excluir(nota.getImagemChave());
            transacao.executeWithoutResult(status -> {
                if (notas.registrarImagemExcluida(id, nota.getUsuario().getId(), nota.getVersao(), Instant.now()) != 1)
                    throw new IllegalStateException("Imagem removida mas estado de limpeza nao persistido");
            });
        } catch (IOException falha) {
            transacao.executeWithoutResult(status -> {
                if (notas.registrarFalhaLimpeza(id, nota.getUsuario().getId(), nota.getVersao(), "FALHA_IO", Instant.now()) != 1)
                    throw new IllegalStateException("Falha de exclusao nao persistida", falha);
            });
        }
    }

    public void aposCommit(Long id) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Limpeza exige transacao de finalizacao");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    limpar(id);
                } catch (RuntimeException falha) {
                    LOG.error("Limpeza imediata falhou; verificar pendencias operacionais");
                }
            }
        });
    }
}
