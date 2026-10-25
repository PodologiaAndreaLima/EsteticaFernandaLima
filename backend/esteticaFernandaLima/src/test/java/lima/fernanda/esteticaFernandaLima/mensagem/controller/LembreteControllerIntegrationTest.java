package lima.fernanda.esteticaFernandaLima.mensagem.controller;

import lima.fernanda.esteticaFernandaLima.mensagem.dto.*;
import lima.fernanda.esteticaFernandaLima.mensagem.service.LembreteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LembreteController.class)
@AutoConfigureMockMvc(addFilters = false)
class LembreteControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean LembreteService service;

    @Test
    void rejeitaPayloadInvalido() throws Exception {
        mockMvc.perform(post("/lembretes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":0,"dataHora":"2020-01-01T10:00:00",
                                 "tipo":"LEMBRETE_AGENDAMENTO"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
