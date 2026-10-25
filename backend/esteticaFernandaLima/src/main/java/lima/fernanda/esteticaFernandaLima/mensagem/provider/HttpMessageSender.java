package lima.fernanda.esteticaFernandaLima.mensagem.provider;

import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
@ConditionalOnProperty(name = "mensagens.provider", havingValue = "http")
public class HttpMessageSender implements MessageSender {
    private final RestTemplate restTemplate;
    private final String token;

    public HttpMessageSender(RestTemplateBuilder builder,
                             @Value("${mensagens.provider.url:}") String url,
                             @Value("${mensagens.provider.token:}") String token,
                             @Value("${mensagens.provider.timeout-ms:10000}") long timeoutMs) {
        if (url.isBlank()) {
            throw new IllegalStateException("MENSAGENS_PROVIDER_URL é obrigatório quando mensagens.provider=http");
        }
        this.token = token;
        this.restTemplate = builder.rootUri(url)
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .readTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Override
    public void send(CanalMensagem canal, String telefone, String conteudo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!token.isBlank()) {
            headers.setBearerAuth(token);
        }
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    "/messages",
                    new HttpEntity<>(new ProviderRequest(canal.name(), telefone, conteudo), headers),
                    Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new MessageProviderException("Provedor de mensagens retornou HTTP "
                        + response.getStatusCode().value());
            }
        } catch (RestClientException ex) {
            throw new MessageProviderException("Falha de comunicação com o provedor de mensagens", ex);
        }
    }

    record ProviderRequest(String canal, String telefone, String conteudo) {}
}
