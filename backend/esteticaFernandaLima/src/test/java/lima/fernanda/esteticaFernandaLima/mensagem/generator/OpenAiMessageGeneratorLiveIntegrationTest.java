package lima.fernanda.esteticaFernandaLima.mensagem.generator;

import lima.fernanda.esteticaFernandaLima.mensagem.model.Lembrete;
import lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

class OpenAiMessageGeneratorLiveIntegrationTest {
    @Test
    void chamaOpenAiRealQuandoConfiguracaoLocalEstaDisponivel() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String enabled = System.getenv("MESSAGENS_AI_ENABLED");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isBlank(),
                "OPENAI_API_KEY não está disponível neste processo");
        Assumptions.assumeTrue("true".equalsIgnoreCase(enabled),
                "MESSAGENS_AI_ENABLED não está true neste processo");

        String model = valueOrDefault(System.getenv("OPENAI_MODEL"), "gpt-4o-mini");
        String baseUrl = valueOrDefault(System.getenv("OPENAI_BASE_URL"), "https://api.openai.com/v1");
        long timeoutMs = parseTimeout(System.getenv("OPENAI_TIMEOUT_MS"));

        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), apiKey, model, baseUrl, timeoutMs);

        Lembrete lembrete = new Lembrete();
        lembrete.setTipo(TipoMensagem.LEMBRETE_AGENDAMENTO);
        lembrete.setDataHora(LocalDateTime.of(2099, 1, 15, 10, 30));
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto("Cliente de teste");
        lembrete.setCliente(cliente);
        lembrete.setProcedimento("avaliação estética");

        String content = generator.generate(lembrete);

        assertFalse(content.isBlank());
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private long parseTimeout(String value) {
        if (value == null || value.isBlank()) {
            return 10000;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("OPENAI_TIMEOUT_MS inválido", exception);
        }
    }
}
