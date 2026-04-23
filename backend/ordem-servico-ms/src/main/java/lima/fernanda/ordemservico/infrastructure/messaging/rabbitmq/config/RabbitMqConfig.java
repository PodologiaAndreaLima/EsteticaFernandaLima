package lima.fernanda.ordemservico.infrastructure.messaging.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de infraestrutura RabbitMQ.
 * Define Exchange, Queues (principal e DLQ) e Bindings.
 * Todos os nomes são configuráveis via application.properties.
 *
 * Se RabbitMQ não estiver disponível durante o desenvolvimento, adicionar:
 * spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
 */
@Configuration
public class RabbitMqConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Value("${rabbitmq.exchange.ordem-servico}")
    private String exchangeName;

    @Value("${rabbitmq.queue.ordem-servico}")
    private String queueName;

    @Value("${rabbitmq.queue.ordem-servico-dlq}")
    private String dlqName;

    @Value("${rabbitmq.routing-key.ordem-servico}")
    private String routingKey;

    @Value("${rabbitmq.routing-key.ordem-servico-dlq}")
    private String dlqRoutingKey;

    // ── Exchange principal ────────────────────────────────────────────────────

    @Bean
    public DirectExchange ordemServicoExchange() {
        return ExchangeBuilder
                .directExchange(exchangeName)
                .durable(true)
                .build();
    }

    // ── Dead Letter Exchange ──────────────────────────────────────────────────

    @Bean
    public DirectExchange ordemServicoDlqExchange() {
        return ExchangeBuilder
                .directExchange(exchangeName + ".dlx")
                .durable(true)
                .build();
    }

    // ── Fila principal com referência à DLX ───────────────────────────────────

    @Bean
    public Queue ordemServicoQueue() {
        return QueueBuilder
                .durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    // ── Dead Letter Queue ─────────────────────────────────────────────────────

    @Bean
    public Queue ordemServicoDlq() {
        return QueueBuilder
                .durable(dlqName)
                .build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────────

    @Bean
    public Binding ordemServicoBinding() {
        return BindingBuilder
                .bind(ordemServicoQueue())
                .to(ordemServicoExchange())
                .with(routingKey);
    }

    @Bean
    public Binding ordemServicoDlqBinding() {
        return BindingBuilder
                .bind(ordemServicoDlq())
                .to(ordemServicoDlqExchange())
                .with(dlqRoutingKey);
    }

    // ── Conversão de mensagens (JSON) ─────────────────────────────────────────

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        try {
            log.info("Criando RabbitAdmin bean");
            return new RabbitAdmin(connectionFactory);
        } catch (Exception ex) {
            log.warn("Falha ao criar RabbitAdmin: {}", ex.getMessage());
            return null;
        }
    }
}
