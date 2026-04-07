package lima.fernanda.esteticaFernandaLima.integration.ordemservico;

import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsPageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrdemServicoMsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrdemServicoMsClient(RestTemplateBuilder restTemplateBuilder,
                                @Value("${ordemservico.ms.base-url:http://localhost:8081}") String baseUrl,
                                @Value("${ordemservico.ms.timeout-ms:5000}") long timeoutMs) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public OrdemServicoMsPageResponse listarPaginado(int page) {
        try {
            String url = baseUrl + "/ordens-servico?page={page}";
            return restTemplate.getForObject(url, OrdemServicoMsPageResponse.class, page);
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao listar ordens no microservico", ex);
        }
    }
}
