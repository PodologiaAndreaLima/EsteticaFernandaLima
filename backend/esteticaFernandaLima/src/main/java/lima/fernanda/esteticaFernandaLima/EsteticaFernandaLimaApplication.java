package lima.fernanda.esteticaFernandaLima;

import lima.fernanda.esteticaFernandaLima.config.NotaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(NotaProperties.class)
public class EsteticaFernandaLimaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsteticaFernandaLimaApplication.class, args);
	}

}
