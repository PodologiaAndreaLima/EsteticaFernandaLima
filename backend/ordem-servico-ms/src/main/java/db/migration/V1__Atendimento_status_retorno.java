package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class V1__Atendimento_status_retorno extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        try (Statement sql = connection.createStatement()) {
            sql.execute("""
                    CREATE TABLE IF NOT EXISTS ordem_servico_ms (
                      id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                      cliente_id INTEGER NOT NULL, usuario_id INTEGER NOT NULL,
                      valor_final FLOAT NOT NULL, data_criacao DATE NOT NULL,
                      observacao VARCHAR(255))
                    """);
            if (!colunaExiste(connection, "dt_hora")) {
                sql.execute("ALTER TABLE ordem_servico_ms ADD COLUMN dt_hora DATETIME NULL");
            } else {
                sql.execute("ALTER TABLE ordem_servico_ms MODIFY COLUMN dt_hora DATETIME NULL");
            }
            sql.execute("UPDATE ordem_servico_ms SET dt_hora = CAST(data_criacao AS DATETIME) WHERE dt_hora IS NULL");
            sql.execute("ALTER TABLE ordem_servico_ms MODIFY COLUMN dt_hora DATETIME NOT NULL");
            if (!colunaExiste(connection, "status")) {
                sql.execute("ALTER TABLE ordem_servico_ms ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT 'PENDENTE'");
            }
            if (!colunaExiste(connection, "retorno")) {
                sql.execute("ALTER TABLE ordem_servico_ms ADD COLUMN retorno BOOLEAN NOT NULL DEFAULT FALSE");
            }
            sql.execute("ALTER TABLE ordem_servico_ms ADD CONSTRAINT chk_os_status CHECK (status IN ('PENDENTE','EM_ANDAMENTO','CONCLUIDA','CANCELADA'))");
        }
    }

    private boolean colunaExiste(Connection connection, String column) throws Exception {
        try (var query = connection.prepareStatement("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE LOWER(table_name) = 'ordem_servico_ms' AND LOWER(column_name) = ?
                  AND LOWER(table_schema) = LOWER(?)
                """)) {
            query.setString(1, column);
            query.setString(2, connection.getMetaData().getDatabaseProductName().equals("H2")
                    ? connection.getSchema() : connection.getCatalog());
            try (ResultSet result = query.executeQuery()) { result.next(); return result.getInt(1) > 0; }
        }
    }
}
