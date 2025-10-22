package lima.fernanda.esteticaFernandaLima.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Estética Fernanda Lima",
                description = "API para gerenciamento de clientes da Estética Fernanda Lima",
                contact = @Contact(
                        name = "Fernanda Lima",
                        email = "contato@esteticafernandalima.com"
                ),
                license = @License(name = "UNLICENSED"),
                version = "1.0"
        )
)
@SecurityScheme(
        name = "Bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("API Estética Fernanda Lima")
                        .description("API para gerenciamento de clientes da Estética Fernanda Lima")
                        .version("1.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Fernanda Lima")
                                .email("contato@esteticafernandalima.com")));
    }
}
