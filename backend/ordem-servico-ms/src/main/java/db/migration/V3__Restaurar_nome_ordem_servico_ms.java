package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Connection;

public class V3__Restaurar_nome_ordem_servico_ms extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        boolean origem = tabelaExiste(connection, "ordem_servico");
        boolean destino = tabelaExiste(connection, "ordem_servico_ms");
        if (origem && destino) {
            // Bancos criados pelo ScriptBD.sql já têm a tabela legada ordem_servico (do backend
            // principal) ao lado de ordem_servico_ms. Nesse caso não há o que renomear: o
            // microsserviço continua usando ordem_servico_ms e a tabela legada fica intacta.
            return;
        }
        if (origem) {
            try (var sql = connection.createStatement()) {
                sql.execute("ALTER TABLE ordem_servico RENAME TO ordem_servico_ms");
            }
        } else if (!destino) {
            throw new IllegalStateException("Tabela de ordens de serviço não encontrada para restauração");
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
