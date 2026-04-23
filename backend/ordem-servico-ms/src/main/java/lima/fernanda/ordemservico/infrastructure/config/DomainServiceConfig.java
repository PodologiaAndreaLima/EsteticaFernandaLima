package lima.fernanda.ordemservico.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lima.fernanda.ordemservico.domain.repository.DashboardOrdemServicoRepositoryPort;
import lima.fernanda.ordemservico.domain.service.OrdemServicoProcessamentoDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra os serviços de domínio como beans Spring.
 * O domínio não depende do Spring — a configuração fica na camada de infraestrutura.
 */
@Configuration
public class DomainServiceConfig {

    /**
     * ObjectMapper configurado com suporte a LocalDate (JavaTimeModule).
     * Usado pelo DomainService para serializar/deserializar itensJson.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public OrdemServicoProcessamentoDomainService ordemServicoProcessamentoDomainService(
            DashboardOrdemServicoRepositoryPort dashboardRepository,
            ObjectMapper objectMapper) {
        return new OrdemServicoProcessamentoDomainService(dashboardRepository, objectMapper);
    }
}
