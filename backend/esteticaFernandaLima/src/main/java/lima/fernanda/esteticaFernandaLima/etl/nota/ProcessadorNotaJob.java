package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.config.NotaProperties;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalJob;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaException;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalImportacaoRepository;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalJobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Component
public class ProcessadorNotaJob {
    private final NotaFiscalJobRepository jobs;
    private final NotaFiscalImportacaoRepository notas;
    private final ArmazenamentoImagemNota armazenamento;
    private final ObjectProvider<ProcessadorImagemNota> provedor;
    private final NotaProperties propriedades;
    private final TransactionTemplate transacao;
    private final LimpezaImagemNota limpeza;

    public ProcessadorNotaJob(NotaFiscalJobRepository jobs, NotaFiscalImportacaoRepository notas,
                              ArmazenamentoImagemNota armazenamento, ObjectProvider<ProcessadorImagemNota> provedor,
                               NotaProperties propriedades, TransactionTemplate transacao, LimpezaImagemNota limpeza) {
        this.jobs = jobs;
        this.notas = notas;
        this.armazenamento = armazenamento;
        this.provedor = provedor;
        this.propriedades = propriedades;
        this.transacao = transacao;
        this.limpeza = limpeza;
    }

    @Scheduled(fixedDelay = 3000)
    public void executar() {
        for (Long id : jobs.esgotados(Instant.now(), propriedades.worker().maxTentativas(),
                PageRequest.of(0, propriedades.worker().concorrencia()))) finalizarEsgotado(id);
        var cliente = provedor.getIfAvailable();
        if (cliente == null || !cliente.disponivel()) return;
        List<Long> candidatos = jobs.candidatos(Instant.now(), propriedades.worker().maxTentativas(),
                PageRequest.of(0, propriedades.worker().concorrencia()));
        for (Long id : candidatos) processar(id, cliente);
    }

    private void finalizarEsgotado(Long id) {
        Boolean finalizada = transacao.execute(status -> {
            var nota = notas.findById(id).orElseThrow(() -> new IllegalStateException("Job sem nota"));
            var registro = notas.bloquearPropria(id, nota.getUsuario().getId()).orElseThrow();
            if (registro.getStatus() != EstadoNota.PROCESSANDO) return false;
            var job = jobs.findById(id).orElseThrow(() -> new IllegalStateException("Job ausente"));
            if (job.getLeaseAte() == null || jobs.encerrarEsgotado(id, job.getLeaseAte(), Instant.now(),
                    propriedades.worker().maxTentativas()) != 1) return false;
            if (notas.finalizar(id, registro.getUsuario().getId(), registro.getVersao(),
                    List.of(EstadoNota.PROCESSANDO), EstadoNota.FALHA, CodigoFalha.PROCESSAMENTO_FALHOU,
                    Instant.now()) != 1) throw new IllegalStateException("Job esgotado sem transicao da nota");
            return true;
        });
        if (Boolean.TRUE.equals(finalizada)) limpeza.limpar(id);
    }

    private void processar(Long id, ProcessadorImagemNota cliente) {
        Instant agora = Instant.now();
        Instant ate = agora.plus(propriedades.worker().lease()).truncatedTo(ChronoUnit.MICROS);
        Integer adquirido = transacao.execute(status -> jobs.adquirir(id, agora, ate, propriedades.worker().maxTentativas()));
        if (adquirido == null || adquirido != 1) return;
        var nota = notas.findById(id).orElseThrow(() -> new IllegalStateException("Job sem nota"));
        try {
            byte[] bytes = armazenamento.ler(nota.getImagemChave());
            cliente.processar(id, nota.getUsuario().getId(), bytes, identificar(bytes));
            var atual = notas.findById(id).orElseThrow(() -> new IllegalStateException("Job sem nota"));
            if (atual.getStatus() == EstadoNota.PROCESSANDO) throw new IllegalStateException("Processamento sem transicao");
            transacao.executeWithoutResult(status -> jobs.encerrar(id, ate, NotaFiscalJob.Estado.CONCLUIDO));
            if (atual.getStatus() == EstadoNota.FALHA) limpeza.limpar(id);
        } catch (Exception falha) {
            var atual = notas.findById(id).orElseThrow(() -> new IllegalStateException("Job sem nota"));
            if (atual.getStatus() != EstadoNota.PROCESSANDO) {
                transacao.executeWithoutResult(status -> jobs.encerrar(id, ate, NotaFiscalJob.Estado.CONCLUIDO));
                return;
            }
            var job = jobs.findById(id).orElseThrow(() -> new IllegalStateException("Job ausente"));
            if (ate.equals(job.getLeaseAte()) && deveFinalizarFalha(falha, job)) {
                CodigoFalha codigo = falha instanceof ExtracaoNotaException extracao
                        ? extracao.codigo() : CodigoFalha.PROCESSAMENTO_FALHOU;
                Boolean finalizada = transacao.execute(status -> {
                    var registro = notas.bloquearPropria(id, nota.getUsuario().getId()).orElseThrow();
                    int alteradas = notas.finalizar(id, registro.getUsuario().getId(), registro.getVersao(),
                            List.of(EstadoNota.PROCESSANDO), EstadoNota.FALHA, codigo, Instant.now());
                    if (alteradas != 1) return false;
                    if (jobs.encerrar(id, ate, NotaFiscalJob.Estado.CONCLUIDO) != 1) {
                        throw new IllegalStateException("Nota finalizada sem encerrar job com lease atual");
                    }
                    return alteradas == 1;
                });
                if (Boolean.TRUE.equals(finalizada)) limpeza.limpar(id);
            }
        }
    }

    private boolean deveFinalizarFalha(Exception falha, NotaFiscalJob job) {
        if (falha instanceof ExtracaoNotaException extracao
                && extracao.codigo() != CodigoFalha.PROVEDOR_INDISPONIVEL) return true;
        return job.getTentativas() >= propriedades.worker().maxTentativas();
    }

    private String identificar(byte[] bytes) {
        if (bytes.length > 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216) return "image/jpeg";
        if (bytes.length > 8 && bytes[0] == (byte) 137 && bytes[1] == 80) return "image/png";
        if (bytes.length > 12 && bytes[0] == 82 && bytes[1] == 73 && bytes[8] == 87) return "image/webp";
        throw new IllegalArgumentException("Assinatura de imagem invalida");
    }
}
