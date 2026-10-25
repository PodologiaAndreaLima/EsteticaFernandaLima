package db.migration;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AtendimentoMigrationTest {
    @Test
    void devePreservarRegistrosAntigosEConverterDataParaMeiaNoite() throws Exception {
        verificar(false);
    }

    @Test
    void deveConverterColunaDtHoraExistenteSemPerderData() throws Exception {
        verificar(true);
    }

    private void verificar(boolean dtExistente) throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:migration-" + dtExistente + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        try (var connection = ds.getConnection(); var sql = connection.createStatement()) {
            sql.execute("CREATE TABLE ordem_servico_ms(id INTEGER PRIMARY KEY, cliente_id INTEGER, usuario_id INTEGER, valor_final FLOAT, data_criacao DATE, observacao VARCHAR(255)" + (dtExistente ? ", dt_hora DATE" : "") + ")");
            sql.execute("INSERT INTO ordem_servico_ms VALUES(7,1,2,150,'2026-10-20','Manter'" + (dtExistente ? ",'2026-11-05'" : "") + ")");
        }
        var flyway = Flyway.configure().dataSource(ds).table("flyway_ordem_servico_history")
                .baselineOnMigrate(true).baselineVersion("0").load();
        flyway.migrate();
        try (var connection = ds.getConnection(); var sql = connection.createStatement();
             var result = sql.executeQuery("SELECT * FROM ordem_servico_ms WHERE id=7")) {
            assertTrue(result.next());
            assertEquals(dtExistente ? "2026-11-05T00:00" : "2026-10-20T00:00", result.getTimestamp("dt_hora").toLocalDateTime().toString());
            assertEquals("PENDENTE", result.getString("status"));
            assertFalse(result.getBoolean("retorno"));
            assertEquals("Manter", result.getString("observacao"));
            assertEquals("2026-10-20", result.getDate("data_criacao").toString());
        }
        assertEquals(0, flyway.migrate().migrationsExecuted);
    }
}
