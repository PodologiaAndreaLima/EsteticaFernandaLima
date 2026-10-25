package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Connection;

public class V2__Nome_tabela_ordem_servico extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        boolean origem = tabelaExiste(connection, "ordem_servico_ms");
        boolean destino = tabelaExiste(connection, "ordem_servico");
        if (origem && destino) {
            // Bancos criados pelo ScriptBD.sql já têm a tabela legada ordem_servico (do backend
            // principal) ao lado de ordem_servico_ms. Nesse caso não há o que renomear: o
            // microsserviço continua usando ordem_servico_ms e a tabela legada fica intacta.
            return;
        }
        if (origem) {
            try (var sql = connection.createStatement()) {
                sql.execute("ALTER TABLE ordem_servico_ms RENAME TO ordem_servico");
            }
        } else if (!destino) {
            throw new IllegalStateException("Tabela de ordens de serviço não encontrada para renomeação");
        }
    }

    private boolean tabelaExiste(Connection connection, String nome) throws Exception {
        try (var query = connection.prepareStatement("""
                SELECT COUNT(*) FROM information_schema.tables
                WHERE LOWER(table_name) = ? AND LOWER(table_schema) = LOWER(?)
                """)) {
            query.setString(1, nome);
            query.setString(2, connection.getMetaData().getDatabaseProductName().equals("H2")
                    ? connection.getSchema() : connection.getCatalog());
            try (var result = query.executeQuery()) { result.next(); return result.getInt(1) > 0; }
        }
    }
}
