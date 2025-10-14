package lima.fernanda.esteticaFernandaLima.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Estética Fernanda Lima")
                        .description("API para gerenciamento de clientes da Estética Fernanda Lima")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Fernanda Lima")
                                .email("contato@esteticafernandalima.com")));
    }
}
