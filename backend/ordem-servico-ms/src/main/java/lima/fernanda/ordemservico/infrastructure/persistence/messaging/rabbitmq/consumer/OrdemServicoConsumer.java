package lima.fernanda.ordemservico.infrastructure.persistence.messaging.rabbitmq.consumer;

import lima.fernanda.ordemservico.application.service.OrdemServicoProcessamentoApplicationService;
import lima.fernanda.ordemservico.domain.event.OrdemServicoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

/**
 * Consumer RabbitMQ para eventos de OrdemServico.
 * Retry automático configurado no RabbitMqConfig (defaultRequeueRejected=false).
 * Falhas definitivas são encaminhadas para a DLQ automaticamente pelo broker.
 */
@Component
public class OrdemServicoConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoConsumer.class);

    private final OrdemServicoProcessamentoApplicationService applicationService;

    public OrdemServicoConsumer(OrdemServicoProcessamentoApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.ordem-servico}", containerFactory = "rabbitListenerContainerFactory")
    public void consumir(OrdemServicoEvent event) {
        log.info("[Consumer] Mensagem recebida: tipoOperacao={}, ordemServicoId={}",
                event.getTipoOperacao(), event.getOrdemServicoId());

        try {
            applicationService.processar(event);
            log.info("[Consumer] Evento processado com sucesso: ordemServicoId={}", event.getOrdemServicoId());
        } catch (ListenerExecutionFailedException ex) {
            log.error("[Consumer] Falha de execução do listener para ordemServicoId={}: {}",
                    event.getOrdemServicoId(), ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[Consumer] Erro ao processar evento para ordemServicoId={}: {}",
                    event.getOrdemServicoId(), ex.getMessage(), ex);
            // Re-lança para ativar o mecanismo de retry/DLQ do Spring AMQP
            throw ex;
        }
    }
}
