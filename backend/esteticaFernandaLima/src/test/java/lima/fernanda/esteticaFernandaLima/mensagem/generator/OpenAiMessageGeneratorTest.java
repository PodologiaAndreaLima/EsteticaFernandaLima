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
                        "Último atendimento: 01/10/2026")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Retorno previsto: 01/11/2026")))
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
    void enviaSoOPrimeiroNomeParaAOpenAi() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Primeiro nome: Mariana")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Souza"))))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana!"}}]}
                        """, MediaType.APPLICATION_JSON));

        Lembrete lembrete = lembrete(TipoMensagem.LEMBRETE_AGENDAMENTO);
        lembrete.getCliente().setNomeCompleto("Mariana Souza Lima");

        assertFalse(generator.generate(lembrete).isBlank());
        server.verify();
    }

    @Test
    void primeiroNomeIgnoraEspacosExtras() {
        assertEquals("Mariana", NomeCliente.primeiroNome("  Mariana   Souza "));
        assertEquals("Ana", NomeCliente.primeiroNome("Ana"));
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

    @Test
    void enviaDataFormatadaNomeDaClinicaETemperatura() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Data e hora: domingo, 25/10/2026 às 14:00")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("2026-10-25T14:00"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Nome da clínica: Fernanda Lima Beauty Care")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"temperature\":0.9")))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana!"}}]}
                        """, MediaType.APPLICATION_JSON));

        assertFalse(generator.generate(lembrete(TipoMensagem.LEMBRETE_AGENDAMENTO)).isBlank());
        server.verify();
    }

    @Test
    void instrucoesDefinemTamanhoLinguagemNeutraEExemplos() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000);
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No máximo 3 frases e até 300 caracteres")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("linguagem neutra quanto a gênero")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no máximo 1 emoji")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("EXEMPLOS (dados fictícios")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nunca reutilize as frases deles")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Variação de estilo: ")))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana!"}}]}
                        """, MediaType.APPLICATION_JSON));

        assertFalse(generator.generate(lembrete(TipoMensagem.RETORNO)).isBlank());
        server.verify();
    }

    @Test
    void usaNomeDaClinicaETemperaturaConfigurados() {
        OpenAiMessageGenerator generator = new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000,
                0.3, "Clínica Teste");
        var restTemplate = (org.springframework.web.client.RestTemplate)
                ReflectionTestUtils.getField(generator, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.openai.test/v1/chat/completions"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nome da clínica: Clínica Teste")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"temperature\":0.3")))
                .andRespond(withSuccess("""
                        {"choices":[{"message":{"role":"assistant","content":"Olá, Mariana!"}}]}
                        """, MediaType.APPLICATION_JSON));

        assertFalse(generator.generate(lembrete(TipoMensagem.LEMBRETE_AGENDAMENTO)).isBlank());
        server.verify();
    }

    @Test
    void rejeitaTemperaturaForaDoIntervalo() {
        assertThrows(IllegalStateException.class, () -> new OpenAiMessageGenerator(
                new RestTemplateBuilder(), "test-key", "gpt-test", "https://api.openai.test/v1", 1000,
                2.5, "Clínica Teste"));
    }

    @Test
    void sorteiaVariacaoDeEstiloDaLista() {
        java.util.Set<String> sorteadas = new java.util.HashSet<>();
        for (int i = 0; i < 200; i++) {
            String variacao = OpenAiMessageGenerator.sortearVariacao();
            assertTrue(OpenAiMessageGenerator.VARIACOES_DE_ESTILO.contains(variacao));
            sorteadas.add(variacao);
        }
        assertTrue(sorteadas.size() > 1, "a variação de estilo deve mudar entre as gerações");
    }

    @Test
    void formataDatasEmPortugues() {
        assertEquals("segunda-feira, 26/10/2026 às 10:00",
                OpenAiMessageGenerator.dataHora(LocalDateTime.of(2026, 10, 26, 10, 0)));
        assertEquals("01/10/2026", OpenAiMessageGenerator.data(java.time.LocalDate.of(2026, 10, 1)));
        assertEquals("não informado", OpenAiMessageGenerator.data(null));
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
