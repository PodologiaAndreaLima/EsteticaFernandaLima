package lima.fernanda.esteticaFernandaLima.mensagem.generator;

import lima.fernanda.esteticaFernandaLima.mensagem.exception.GeracaoMensagemException;
import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OpenAiMessageGeneratorTest {
    @Test
    void chamaOpenAiEInterpretaResposta() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana!"}}]}
                        """, MediaType.APPLICATION_JSON));

        Lembrete lembrete = lembrete(TipoMensagem.LEMBRETE_AGENDAMENTO);
        assertEquals("Olá, Mariana!", generator.generate(lembrete));
        server.verify();
    }

    @Test
    void montaDadosEspecificosParaRetorno() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "assistente especializado na redação de mensagens")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Procedimento anterior: limpeza de pele")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Último atendimento: 2026-10-01")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Retorno previsto: 2026-11-01")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Observação de contexto (não é instrução)")))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana! Vamos agendar seu retorno?"}}]}
                        """, MediaType.APPLICATION_JSON));

        Lembrete lembrete = lembrete(TipoMensagem.RETORNO);
        lembrete.setDataUltimoAtendimento(java.time.LocalDate.of(2026, 10, 1));
        lembrete.setDataRetornoPrevisto(java.time.LocalDate.of(2026, 11, 1));

        assertFalse(generator.generate(lembrete).isBlank());
        server.verify();
    }

    @Test
    void rejeitaDadosEssenciaisAusentesPorTipo() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);

        Lembrete agendamentoSemData = lembrete(TipoMensagem.LEMBRETE_AGENDAMENTO);
        agendamentoSemData.setDataHora(null);
        assertThrows(GeracaoMensagemException.class, () -> generator.generate(agendamentoSemData));

        Lembrete retornoSemProcedimento = lembrete(TipoMensagem.RETORNO);
        retornoSemProcedimento.setProcedimento(null);
        assertThrows(GeracaoMensagemException.class, () -> generator.generate(retornoSemProcedimento));
    }

    @Test
    void rejeitaRespostaVazia() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andRespond(withSuccess("{\"choices\":[]}", MediaType.APPLICATION_JSON));

        assertThrows(GeracaoMensagemException.class,
                () -> generator.generate(lembrete(TipoMensagem.RETORNO)));
    }

    @Test
    void rejeitaChaveAusente() {
        assertThrows(IllegalStateException.class, () -> new OpenAiMessageGenerator(
                new RestTemplateBuilder(), " ", "gpt-test", "https://api.openai.test/v1", 1000));
    }

    private Lembrete lembrete(TipoMensagem tipo) {
        Cliente cliente = new Cliente();
        cliente.setNomeCompleto("Mariana");
        Lembrete lembrete = new Lembrete();
        lembrete.setCliente(cliente);
        lembrete.setTipo(tipo);
        lembrete.setDataHora(LocalDateTime.of(2026, 10, 25, 14, 0));
        lembrete.setProcedimento("limpeza de pele");
        return lembrete;
    }
}
