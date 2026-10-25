package lima.fernanda.ordemservico.adapters.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.ordemservico.application.dto.OrdemServicoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoEventPublisherPort;
import java.time.LocalDateTime;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrdemServicoControllerIT {

    @MockitoBean
    private OrdemServicoEventPublisherPort publisher;

    @MockitoBean(name = "org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry")
    private org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry rabbitListeners;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private String dados(boolean retorno, String dataHora) {
        return """
                {"clienteId":1,"usuarioId":1,"valorFinal":150,"observacao":"Teste",
                "itens":[],"dt_hora":"%s","retorno":%s,"status":"CONCLUIDA"}
                """.formatted(dataHora, retorno);
    }

    private int criar(boolean retorno) throws Exception {
        String response = mockMvc.perform(post("/ordens-servico").contentType(MediaType.APPLICATION_JSON)
                        .content(dados(retorno, "2026-11-05T14:30:00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.retorno").value(retorno))
                .andExpect(jsonPath("$.dt_hora").value(retorno ? "2026-11-05T00:00:00" : "2026-11-05T14:30:00"))
                .andReturn().getResponse().getContentAsString();
        entityManager.flush(); entityManager.clear();
        return objectMapper.readTree(response).get("id").asInt();
    }

    @Test
    void devePersistirDataHoraEStatusInicial() throws Exception {
        int id = criar(false);
        mockMvc.perform(get("/ordens-servico/" + id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dt_hora").value("2026-11-05T14:30:00"))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void devePersistirRetornoSemAlterarDataHoraNaEdicao() throws Exception {
        int id = criar(true);
        mockMvc.perform(put("/ordens-servico/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(dados(true, "2026-11-05T00:00:00").replace("CONCLUIDA", "PENDENTE")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dt_hora").value("2026-11-05T00:00:00"));
        mockMvc.perform(put("/ordens-servico/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(dados(true, "2026-11-06T00:00:00")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarHorarioDoRetornoSomenteAoIniciarOuCancelar() throws Exception {
        for (String destino : new String[]{"EM_ANDAMENTO", "CANCELADA"}) {
            int id = criar(true);
            mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"" + destino + "\",\"dt_hora\":\"2026-11-05T09:45:00\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(destino));
            entityManager.flush(); entityManager.clear();
            mockMvc.perform(get("/ordens-servico/" + id)).andExpect(jsonPath("$.dt_hora").value("2026-11-05T09:45:00"));
        }
    }

    @Test
    void devePreservarHorarioSemSolicitacaoExplicita() throws Exception {
        int id = criar(true);
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_ANDAMENTO\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dt_hora").value("2026-11-05T00:00:00"));
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONCLUIDA\",\"dt_hora\":\"2026-11-05T10:00:00\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONCLUIDA\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dt_hora").value("2026-11-05T00:00:00"));
    }

    @Test
    void deveRejeitarStatusInvalidoTransicaoInvalidaEAlteracaoDeData() throws Exception {
        int id = criar(true);
        for (String content : new String[]{"{\"status\":\"INVALIDO\"}", "{\"status\":\"CONCLUIDA\"}",
                "{\"status\":\"EM_ANDAMENTO\",\"dt_hora\":\"2026-11-06T10:00:00\"}",
                "{\"status\":\"PENDENTE\",\"dt_hora\":\"2026-11-05T10:00:00\"}"}) {
            mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON).content(content))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void deveRejeitarMudancaDeHorarioComumPelaRotaDeStatus() throws Exception {
        int id = criar(false);
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_ANDAMENTO\",\"dt_hora\":\"2026-11-05T10:00:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAplicarPadroesQuandoStatusERetornoSaoOmitidos() throws Exception {
        mockMvc.perform(post("/ordens-servico").contentType(MediaType.APPLICATION_JSON)
                        .content(dados(false, "2026-11-05T14:00:00").replace(",\"retorno\":false,\"status\":\"CONCLUIDA\"", "")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.retorno").value(false));
    }

    @Test
    void naoDeveReabrirOrdemCancelada() throws Exception {
        int id = criar(false);
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CANCELADA\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/ordens-servico/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_ANDAMENTO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveExigirDataHoraEValidarFormato() throws Exception {
        for (String content : new String[]{dados(false, "2026-02-30T14:00:00"),
                dados(false, "2026-11-05T14:00:00").replace("\"dt_hora\":\"2026-11-05T14:00:00\",", ""),
                dados(false, "2026-11-05"), dados(false, "2026-11-05T25:00:00")}) {
            mockMvc.perform(post("/ordens-servico").contentType(MediaType.APPLICATION_JSON).content(content))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void deveEditarOrdemComumPreservandoCriacaoEExcluir() throws Exception {
        int id = criar(false);
        entityManager.createNativeQuery("UPDATE ordem_servico_ms SET data_criacao='2025-03-02' WHERE id=:id")
                .setParameter("id", id).executeUpdate();
        entityManager.clear();
        mockMvc.perform(put("/ordens-servico/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(dados(false, "2026-11-10T16:20:00").replace("CONCLUIDA", "PENDENTE")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.dt_hora").value("2026-11-10T16:20:00"))
                .andExpect(jsonPath("$.dataCriacao").value("2025-03-02"));
        mockMvc.perform(delete("/ordens-servico/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/ordens-servico/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void deveListarComPaginacaoFixaDeDez() throws Exception {
        for (int i = 0; i < 11; i++) {
            OrdemServicoRequest request = new OrdemServicoRequest(
                    1,
                    1,
                    100.0f + i,
                    "OS " + i,
                    Collections.emptyList(),
                    LocalDateTime.of(2026, 10, 24, 14, 30), null, false
            );

            mockMvc.perform(post("/ordens-servico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/ordens-servico").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(11));
    }
}

