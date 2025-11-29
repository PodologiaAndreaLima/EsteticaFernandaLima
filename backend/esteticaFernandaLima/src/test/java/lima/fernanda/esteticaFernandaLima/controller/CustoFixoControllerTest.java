package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.service.CustoFixoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustoFixoControllerTest {

    @Mock
    private CustoFixoService service;

    @InjectMocks
    private CustoFixoController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private CustoFixo custoFixo;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
        }

        custoFixo = new CustoFixo();
        custoFixo.setIdCustoFixo(1);
        custoFixo.setNome("Aluguel");
        custoFixo.setDescricao("Aluguel do salão");
        custoFixo.setValorMensal(2000.00f);
    }

    @Test
    void listarTodos() throws Exception {
        List<CustoFixo> custosFixos = List.of(custoFixo);
        when(service.listarTodos()).thenReturn(custosFixos);

        mockMvc.perform(get("/custos-fixos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCustoFixo").value(1))
                .andExpect(jsonPath("$[0].nome").value("Aluguel"))
                .andExpect(jsonPath("$[0].descricao").value("Aluguel do salão"))
                .andExpect(jsonPath("$[0].valorMensal").value(2000.00f));

        verify(service).listarTodos();
    }

    @Test
    void listarTodosVazio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/custos-fixos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).listarTodos();
    }

    @Test
    void postCustoFixo() throws Exception {
        when(service.salvar(any(CustoFixo.class))).thenReturn(custoFixo);

        mockMvc.perform(post("/custos-fixos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCustoFixo").value(1))
                .andExpect(jsonPath("$.nome").value("Aluguel"));

        verify(service).salvar(any(CustoFixo.class));
    }

    @Test
    void deleteCustoFixoPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/custos-fixos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteCustoFixoPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Custo Fixo não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/custos-fixos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void putCustoFixo() throws Exception {
        custoFixo.setNome("Aluguel Premium");
        when(service.atualizar(eq(1), any(CustoFixo.class))).thenReturn(custoFixo);

        mockMvc.perform(put("/custos-fixos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustoFixo").value(1))
                .andExpect(jsonPath("$.nome").value("Aluguel Premium"));

        verify(service).atualizar(eq(1), any(CustoFixo.class));
    }

    @Test
    void putCustoFixoNotFound() throws Exception {
        when(service.atualizar(eq(1), any(CustoFixo.class)))
                .thenThrow(new RuntimeException("Custo Fixo não encontrado"));

        mockMvc.perform(put("/custos-fixos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixo)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(CustoFixo.class));
    }
}