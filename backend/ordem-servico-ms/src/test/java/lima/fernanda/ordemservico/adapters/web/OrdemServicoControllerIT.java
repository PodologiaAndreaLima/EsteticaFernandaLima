package lima.fernanda.ordemservico.adapters.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.ordemservico.application.dto.OrdemServicoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrdemServicoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveListarComPaginacaoFixaDeDez() throws Exception {
        for (int i = 0; i < 11; i++) {
            OrdemServicoRequest request = new OrdemServicoRequest(
                    1,
                    1,
                    100.0f + i,
                    "OS " + i,
                    Collections.emptyList()
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

