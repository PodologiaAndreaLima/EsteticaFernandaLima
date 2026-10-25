package lima.fernanda.ordemservico.adapters.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.datasource.url=jdbc:h2:mem:ordens-seed;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles("test")
@Transactional
class OrdemServicoSeedTest {
    @MockitoBean(name = "org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry")
    private org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry rabbitListeners;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void deveCarregarExemplosDepoisDaMigracaoSemDuplicarOuSobrescrever() {
        assertEquals(5, jdbc.queryForObject("SELECT COUNT(*) FROM ordem_servico_ms", Integer.class));
        assertEquals(4, jdbc.queryForObject("SELECT COUNT(DISTINCT status) FROM ordem_servico_ms", Integer.class));
        assertEquals("2026-11-05T00:00", jdbc.queryForObject(
                "SELECT dt_hora FROM ordem_servico_ms WHERE id=1005 AND retorno=TRUE", java.sql.Timestamp.class)
                .toLocalDateTime().toString());
        jdbc.update("UPDATE ordem_servico_ms SET observacao='Editada pelo usuario', status='CONCLUIDA' WHERE id=1001");
        new ResourceDatabasePopulator(new ClassPathResource("data.sql")).execute(jdbc.getDataSource());
        assertEquals(5, jdbc.queryForObject("SELECT COUNT(*) FROM ordem_servico_ms", Integer.class));
        assertEquals("Editada pelo usuario", jdbc.queryForObject("SELECT observacao FROM ordem_servico_ms WHERE id=1001", String.class));
        assertEquals("CONCLUIDA", jdbc.queryForObject("SELECT status FROM ordem_servico_ms WHERE id=1001", String.class));
    }
}
