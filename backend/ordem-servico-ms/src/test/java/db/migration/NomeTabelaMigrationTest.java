package db.migration;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NomeTabelaMigrationTest {
    private JdbcDataSource banco(String nome) {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + nome + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(ds).table("flyway_ordem_servico_history").target("1").load().migrate();
        return ds;
    }

    @Test
    void deveRenomearTabelaExistentePreservandoIdsDadosEVinculos() throws Exception {
        var ds = banco("renomear-os");
        try (var connection = ds.getConnection(); var sql = connection.createStatement()) {
            sql.execute("INSERT INTO ordem_servico_ms (id,cliente_id,usuario_id,valor_final,data_criacao,dt_hora,status,retorno,observacao) "
                    + "VALUES (7,1,2,150,'2026-10-20','2026-11-05 09:45:00','EM_ANDAMENTO',TRUE,'Preservar')");
            sql.execute("CREATE TABLE ordem_servico_item_ms(id INTEGER PRIMARY KEY, ordem_servico_id INTEGER REFERENCES ordem_servico_ms(id))");
            sql.execute("INSERT INTO ordem_servico_item_ms VALUES (8,7)");
        }
        Flyway.configure().dataSource(ds).table("flyway_ordem_servico_history").target("2").load().migrate();
        try (var connection = ds.getConnection(); var sql = connection.createStatement();
             var result = sql.executeQuery("SELECT o.*, i.id AS item_id FROM ordem_servico o JOIN ordem_servico_item_ms i ON i.ordem_servico_id=o.id")) {
            assertTrue(result.next());
            assertEquals(7, result.getInt("id"));
            assertEquals(8, result.getInt("item_id"));
            assertEquals("Preservar", result.getString("observacao"));
            assertEquals("EM_ANDAMENTO", result.getString("status"));
            assertTrue(result.getBoolean("retorno"));
            assertEquals("2026-11-05T09:45", result.getTimestamp("dt_hora").toLocalDateTime().toString());
            assertThrows(java.sql.SQLException.class, () -> sql.executeQuery("SELECT * FROM ordem_servico_ms"));
        }
        try (var connection = ds.getConnection(); var sql = connection.createStatement()) {
            assertThrows(java.sql.SQLException.class, () -> sql.execute("INSERT INTO ordem_servico_item_ms VALUES (9,999)"));
        }
    }

    @Test
    void mantemAsDuasTabelasQuandoOsDoisNomesExistirem() throws Exception {
        var ds = banco("conflito-nome-os");
        try (var connection = ds.getConnection(); var sql = connection.createStatement()) {
            sql.execute("CREATE TABLE ordem_servico (id INTEGER PRIMARY KEY, observacao VARCHAR(255))");
            sql.execute("INSERT INTO ordem_servico VALUES(7,'Registro legado')");
        }
        // Banco criado pelo ScriptBD.sql: tabela legada ordem_servico + ordem_servico_ms.
        // V2 e V3 não renomeiam nada, a inicialização segue e nenhum dado é sobrescrito.
        assertDoesNotThrow(() -> Flyway.configure().dataSource(ds)
                .table("flyway_ordem_servico_history").load().migrate());
        try (var connection = ds.getConnection(); var sql = connection.createStatement();
             var result = sql.executeQuery("SELECT observacao FROM ordem_servico WHERE id=7")) {
            assertTrue(result.next());
            assertEquals("Registro legado", result.getString(1));
            try (var source = connection.createStatement(); var count = source.executeQuery("SELECT COUNT(*) FROM ordem_servico_ms")) {
                assertTrue(count.next());
            }
        }
    }

    @Test
    void deveRestaurarNomeMsDepoisDaV2PreservandoDadosEVinculos() throws Exception {
        var ds = banco("restaurar-nome-ms");
        Flyway.configure().dataSource(ds).table("flyway_ordem_servico_history").target("2").load().migrate();
        try (var connection = ds.getConnection(); var sql = connection.createStatement()) {
            sql.execute("INSERT INTO ordem_servico (id,cliente_id,usuario_id,valor_final,data_criacao,dt_hora,status,retorno,observacao) "
                    + "VALUES (7,1,2,150,'2026-10-20','2026-11-05 09:45:00','EM_ANDAMENTO',TRUE,'Preservar')");
            sql.execute("CREATE TABLE ordem_servico_item_ms(id INTEGER PRIMARY KEY, ordem_servico_id INTEGER REFERENCES ordem_servico(id))");
            sql.execute("INSERT INTO ordem_servico_item_ms VALUES (8,7)");
        }
        var flyway = Flyway.configure().dataSource(ds).table("flyway_ordem_servico_history").load();
        flyway.migrate();
        try (var connection = ds.getConnection(); var sql = connection.createStatement();
             var result = sql.executeQuery("SELECT o.*, i.id AS item_id FROM ordem_servico_ms o JOIN ordem_servico_item_ms i ON i.ordem_servico_id=o.id")) {
            assertTrue(result.next());
            assertEquals(7, result.getInt("id"));
            assertEquals(8, result.getInt("item_id"));
            assertEquals("Preservar", result.getString("observacao"));
            assertEquals("EM_ANDAMENTO", result.getString("status"));
            assertTrue(result.getBoolean("retorno"));
            assertEquals("2026-11-05T09:45", result.getTimestamp("dt_hora").toLocalDateTime().toString());
            assertThrows(java.sql.SQLException.class, () -> sql.executeQuery("SELECT * FROM ordem_servico"));
        }
        assertEquals(0, flyway.migrate().migrationsExecuted);
    }
}
