package lima.fernanda.ordemservico.infrastructure.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SqlInitializationProperties.class)
public class OrdemServicoDataConfig {
    @Bean
    @DependsOn("flywayInitializer")
    SqlDataSourceScriptDatabaseInitializer ordemServicoDataInitializer(
            DataSource dataSource, SqlInitializationProperties properties) {
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties);
    }
}
