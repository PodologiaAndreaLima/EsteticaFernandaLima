package lima.fernanda.esteticaFernandaLima.integration.ordemservico;

import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsPageResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsRequest;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;


@Component
public class OrdemServicoMsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrdemServicoMsClient(RestTemplateBuilder restTemplateBuilder,
                                @Value("${ordemservico.ms.base-url:http://localhost:8081}") String baseUrl,
                                @Value("${ordemservico.ms.timeout-ms:5000}") long timeoutMs) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .readTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    public OrdemServicoMsPageResponse listarPaginado(int page) {
        try {
            String url = baseUrl + "/ordens-servico?page={page}";
            return restTemplate.getForObject(url, OrdemServicoMsPageResponse.class, page);
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao listar ordens no microservico", ex);
        }
    }

    /** Percorre todas as páginas da listagem do microsserviço (que tem tamanho fixo). */
    public java.util.List<OrdemServicoMsResponse> listarTodas() {
        java.util.List<OrdemServicoMsResponse> todas = new java.util.ArrayList<>();
        for (int page = 0; page < 500; page++) {
            OrdemServicoMsPageResponse resposta = listarPaginado(page);
            if (resposta == null || resposta.getContent() == null) break;
            todas.addAll(resposta.getContent());
            if (page + 1 >= resposta.getTotalPages()) break;
        }
        return todas;
    }

    public OrdemServicoMsResponse criar(OrdemServicoMsRequest request) {
        try {
            return restTemplate.postForObject(
                    baseUrl + "/ordens-servico",
                    request,
                    OrdemServicoMsResponse.class);
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao criar ordem no microservico", ex);
        }
    }

    public OrdemServicoMsResponse atualizar(Integer id, OrdemServicoMsRequest request) {
        try {
            return restTemplate.exchange(
                    baseUrl + "/ordens-servico/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(request),
                    OrdemServicoMsResponse.class,
                    Map.of("id", id)
            ).getBody();
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao atualizar ordem no microservico", ex);
        }
    }

    public OrdemServicoMsResponse buscarPorId(Integer id) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/ordens-servico/{id}",
                    OrdemServicoMsResponse.class,
                    id);
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar ordem no microservico", ex);
        }
    }

    public void deletar(Integer id) {
        try {
            restTemplate.delete(baseUrl + "/ordens-servico/{id}", id);
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao deletar ordem no microservico", ex);
        }
    }

    // ── Endpoints de dashboard pré-calculado ──────────────────────────────

    public Float getReceitaBruta(int mes, int ano) {
        return restTemplate.getForObject(
                baseUrl + "/dashboard/receita-bruta/{mes}/{ano}",
                Float.class, mes, ano);
    }

    public Float getReceitaLiquida(int mes, int ano, float custosFixos, float custosExtras) {
        return restTemplate.getForObject(
                baseUrl + "/dashboard/receita-liquida/{mes}/{ano}?custosFixos={cf}&custosExtras={ce}",
                Float.class, mes, ano, custosFixos, custosExtras);
    }
}
