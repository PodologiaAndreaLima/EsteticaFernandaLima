package lima.fernanda.ordemservico.infrastructure.messaging.rabbitmq.test;

import lima.fernanda.ordemservico.domain.event.OrdemServicoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração para testes simples de RabbitMQ.
 *
 * ATIVAR APENAS EM DESENVOLVIMENTO!
 *
 * Para usar:
 * 1. Descomentar @Configuration nesta classe
 * 2. Adicionar --spring.profiles.active=test ao executar
 * 3. Enviar mensagem de teste ao iniciar
 */
@Configuration
@Profile("test-rabbitmq")
public class RabbitMqTestConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqTestConfiguration.class);

    @Value("${rabbitmq.exchange.ordem-servico}")
    private String exchange;

    @Value("${rabbitmq.routing-key.ordem-servico}")
    private String routingKey;

    @Bean
    public CommandLineRunner testRabbitMq(RabbitTemplate rabbitTemplate) {
        return args -> {
            log.info("🧪 Iniciando teste de RabbitMQ...");

            try {
                // Criar evento de teste
                OrdemServicoEvent testEvent = new OrdemServicoEvent(
                    999,                                        // ID
                    1,                                          // Cliente ID
                    1,                                          // Usuário ID
                    100.0f,                                     // Valor (Float)
                    java.time.LocalDate.now(),                 // Data
                    OrdemServicoEvent.TipoOperacao.CREATE       // Tipo
                );

                // Publicar evento de teste
                log.info("📤 Publicando evento de teste...");
                rabbitTemplate.convertAndSend(exchange, routingKey, testEvent);

                log.info("✅ Evento de teste publicado com sucesso!");
                log.info("💡 Dica: Monitorar fila em http://localhost:15672");

            } catch (Exception ex) {
                log.error("❌ Falha ao publicar evento de teste: {}", ex.getMessage(), ex);
            }
        };
    }
}

