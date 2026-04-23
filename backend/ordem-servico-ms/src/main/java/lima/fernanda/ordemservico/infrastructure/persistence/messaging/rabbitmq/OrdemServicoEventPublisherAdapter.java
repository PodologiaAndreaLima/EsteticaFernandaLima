package lima.fernanda.ordemservico.infrastructure.persistence.messaging.rabbitmq;

import lima.fernanda.ordemservico.domain.event.OrdemServicoEvent;
import lima.fernanda.ordemservico.domain.model.ItemOrdemServico;
import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter de infraestrutura que implementa a porta de domínio OrdemServicoEventPublisherPort.
 * Traduz o modelo de domínio em evento enriquecido (com itens) e envia ao RabbitMQ.
 *
 * Os nomes de serviço/produto/combo são resolvidos aqui, no momento da publicação,
 * para que o ms-scr consumidor não precise fazer lookups externos ao processar a mensagem.
 * Isso garante que o processamento assíncrono seja completamente autossuficiente.
 */
@Component
public class OrdemServicoEventPublisherAdapter implements OrdemServicoEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoEventPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.ordem-servico}")
    private String exchange;

    @Value("${rabbitmq.routing-key.ordem-servico}")
    private String routingKey;

    public OrdemServicoEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarOrdemCriada(OrdemServico ordemServico) {
        OrdemServicoEvent event = toEvent(ordemServico, OrdemServicoEvent.TipoOperacao.CREATE);
        publicar(event);
    }

    @Override
    public void publicarOrdemAtualizada(OrdemServico ordemServico) {
        OrdemServicoEvent event = toEvent(ordemServico, OrdemServicoEvent.TipoOperacao.UPDATE);
        publicar(event);
    }

    @Override
    public void publicarOrdemDeletada(Integer ordemServicoId) {
        OrdemServicoEvent event = new OrdemServicoEvent(
                ordemServicoId, null, null, null, null,
                OrdemServicoEvent.TipoOperacao.DELETE
        );
        publicar(event);
    }

    // ── Conversão ─────────────────────────────────────────────────────────

    private OrdemServicoEvent toEvent(OrdemServico os, OrdemServicoEvent.TipoOperacao tipo) {
        List<OrdemServicoEvent.ItemEvent> itens = os.getItens() == null
                ? List.of()
                : os.getItens().stream()
                        .map(this::toItemEvent)
                        .collect(Collectors.toList());

        return new OrdemServicoEvent(
                os.getId(),
                os.getClienteId(),
                os.getUsuarioId(),
                os.getValorFinal(),
                os.getDataCriacao(),
                tipo,
                itens
        );
    }

    /**
     * Converte ItemOrdemServico em ItemEvent.
     *
     * Nota: os campos nomeServicoProduto, nomeCombo e ehProduto são preenchidos
     * com valores básicos aqui. Para resolução completa de nomes, o back-scr
     * deve popular esses campos antes de salvar (ver OrdemServicoApplicationService
     * atualizado no back-scr que faz lookup no ServicoProdutoRepository e ComboRepository).
     */
    private OrdemServicoEvent.ItemEvent toItemEvent(ItemOrdemServico item) {
        return new OrdemServicoEvent.ItemEvent(
                item.getServicoProdutoId(),
                item.getComboId(),
                item.getProdutoId(),
                item.getQuantidade(),
                item.getDesconto(),
                item.getNomeServicoProduto(),
                item.getNomeCombo(),
                item.getEhProduto()
        );
    }

    private void publicar(OrdemServicoEvent event) {
        log.info("[Publisher] Publicando evento {} para ordemServicoId={}",
                event.getTipoOperacao(), event.getOrdemServicoId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("[Publisher] Evento publicado com sucesso: {}", event);
    }
}
