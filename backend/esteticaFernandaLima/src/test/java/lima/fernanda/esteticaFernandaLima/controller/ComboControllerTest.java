package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.service.ComboService;
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

class ComboControllerTest {

    @Mock
    private ComboService service;

    @InjectMocks
    private ComboController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Combo combo;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
        }

        combo = new Combo();
        combo.setIdCombo(1);
        combo.setNome("Pacote Facial");
        combo.setDescricao("Limpeza + Hidratação");
        combo.setValorFinal(150.00f);
    }

    @Test
    void getCombos() throws Exception {
        List<Combo> combos = List.of(combo);
        when(service.buscarTodos()).thenReturn(combos);

        mockMvc.perform(get("/combo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCombo").value(1))
                .andExpect(jsonPath("$[0].nome").value("Pacote Facial"))
                .andExpect(jsonPath("$[0].descricao").value("Limpeza + Hidratação"))
                .andExpect(jsonPath("$[0].valorFinal").value(150.00f));

        verify(service).buscarTodos();
    }

    @Test
    void getCombosVazio() throws Exception {
        when(service.buscarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/combo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).buscarTodos();
    }

    @Test
    void getComboPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(combo);

        mockMvc.perform(get("/combo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCombo").value(1))
                .andExpect(jsonPath("$.nome").value("Pacote Facial"))
                .andExpect(jsonPath("$.descricao").value("Limpeza + Hidratação"))
                .andExpect(jsonPath("$.valorFinal").value(150.00f));

        verify(service).buscarPorId(1);
    }

    @Test
    void getComboPorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new RuntimeException("Combo não encontrado"));

        mockMvc.perform(get("/combo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

    @Test
    void postCombo() throws Exception {
        when(service.salvar(any(Combo.class))).thenReturn(combo);

        mockMvc.perform(post("/combo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(combo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCombo").value(1))
                .andExpect(jsonPath("$.nome").value("Pacote Facial"));

        verify(service).salvar(any(Combo.class));
    }

    @Test
    void deleteComboPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/combo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteComboPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Combo não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/combo/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void putCombo() throws Exception {
        combo.setNome("Pacote Facial Premium");
        when(service.atualizar(eq(1), any(Combo.class))).thenReturn(combo);

        mockMvc.perform(put("/combo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(combo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCombo").value(1))
                .andExpect(jsonPath("$.nome").value("Pacote Facial Premium"));

        verify(service).atualizar(eq(1), any(Combo.class));
    }

    @Test
    void putComboNotFound() throws Exception {
        when(service.atualizar(eq(1), any(Combo.class)))
                .thenThrow(new RuntimeException("Combo não encontrado"));

        mockMvc.perform(put("/combo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(combo)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(Combo.class));
    }
}