package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.service.CustoExtraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustoExtraControllerTest {

    @Mock
    private CustoExtraService service;

    @InjectMocks
    private CustoExtraController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CustoExtra custoExtra;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }

        custoExtra = new CustoExtra();
        custoExtra.setIdCustoExtra(1);
        custoExtra.setNome("Material Extras");
        custoExtra.setDescricao("Produtos adicionais");
        custoExtra.setValor(50.00f);
        custoExtra.setData(LocalDate.now());
    }

    @Test
    void listarTodos() throws Exception {
        List<CustoExtra> custosExtras = List.of(custoExtra);
        when(service.listarTodos()).thenReturn(custosExtras);

        mockMvc.perform(get("/custos-extras")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCustoExtra").value(1))
                .andExpect(jsonPath("$[0].nome").value("Material Extras"))
                .andExpect(jsonPath("$[0].descricao").value("Produtos adicionais"))
                .andExpect(jsonPath("$[0].valor").value(50.00f));

        verify(service).listarTodos();
    }

    @Test
    void listarTodosVazio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/custos-extras")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).listarTodos();
    }

    @Test
    void criarCustoExtra() throws Exception {
        when(service.salvar(any(CustoExtra.class))).thenReturn(custoExtra);

        mockMvc.perform(post("/custos-extras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoExtra)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCustoExtra").value(1))
                .andExpect(jsonPath("$.nome").value("Material Extras"));

        verify(service).salvar(any(CustoExtra.class));
    }

    @Test
    void deletarCustoExtra() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/custos-extras/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deletarCustoExtraNotFound() throws Exception {
        doThrow(new RuntimeException("Custo Extra não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/custos-extras/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void atualizarCustoExtra() throws Exception {
        custoExtra.setNome("Material Extras Premium");
        when(service.atualizar(eq(1), any(CustoExtra.class))).thenReturn(custoExtra);

        mockMvc.perform(put("/custos-extras/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoExtra)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustoExtra").value(1))
                .andExpect(jsonPath("$.nome").value("Material Extras Premium"));

        verify(service).atualizar(eq(1), any(CustoExtra.class));
    }

    @Test
    void atualizarCustoExtraNotFound() throws Exception {
        when(service.atualizar(eq(1), any(CustoExtra.class)))
                .thenThrow(new RuntimeException("Custo Extra não encontrado"));

        mockMvc.perform(put("/custos-extras/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoExtra)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(CustoExtra.class));
    }
}