package lima.fernanda.ordemservico.infrastructure.messaging.rabbitmq.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Check customizado para RabbitMQ.
 * Monitora a saúde da conexão com RabbitMQ.
 *
 * Endpoint: GET /actuator/health/rabbitmq
 *
 * Se RabbitMQ não estiver disponível, desabilitar com:
 * spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
 */
@Component("rabbitmq")
public class RabbitMqHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqHealthIndicator.class);
    private final RabbitAdmin rabbitAdmin;

    public RabbitMqHealthIndicator(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public Health health() {
        try {
            if (rabbitAdmin == null) {
                return Health.outOfService()
                        .withDetail("status", "RabbitMQ not configured")
                        .build();
            }

            rabbitAdmin.getQueueProperties("ordem-servico.queue");
            log.debug("RabbitMQ Health: UP");
            return Health.up()
                    .withDetail("status", "Connected to RabbitMQ")
                    .build();
        } catch (Exception ex) {
            log.warn("RabbitMQ Health: DOWN - {}", ex.getMessage());
            return Health.down()
                    .withDetail("status", "Unable to connect to RabbitMQ")
                    .withDetail("error", ex.getClass().getSimpleName() + ": " + ex.getMessage())
                    .build();
        }
    }
}

