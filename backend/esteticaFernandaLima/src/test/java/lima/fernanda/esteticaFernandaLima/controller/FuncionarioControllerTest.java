package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.service.FuncionarioService;
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

class FuncionarioControllerTest {

    @Mock
    private FuncionarioService service;

    @InjectMocks
    private FuncionarioController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
        }

        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        funcionario.setNome("Ana Silva");
        funcionario.setEmail("ana@email.com");
        funcionario.setTelefone("11987654321");
        funcionario.setCPF("53428213807");
        funcionario.setSenha("senha123");
        funcionario.setDescricao("Esteticista");
    }

    @Test
    void getFuncionarios() throws Exception {
        List<Funcionario> funcionarios = List.of(funcionario);
        when(service.buscarTodos()).thenReturn(funcionarios);

        mockMvc.perform(get("/funcionarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFuncionario").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ana Silva"))
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));

        verify(service).buscarTodos();
    }

    @Test
    void getFuncionariosVazio() throws Exception {
        when(service.buscarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/funcionarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).buscarTodos();
    }

    @Test
    void getFuncionarioPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(funcionario);

        mockMvc.perform(get("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana@email.com"));

        verify(service).buscarPorId(1);
    }

    @Test
    void getFuncionarioPorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        mockMvc.perform(get("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

    @Test
    void postFuncionario() throws Exception {
        when(service.salvar(any(Funcionario.class))).thenReturn(funcionario);

        mockMvc.perform(post("/funcionarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"));

        verify(service).salvar(any(Funcionario.class));
    }

    @Test
    void deleteFuncionarioPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteFuncionarioPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Funcionário não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void putFuncionario() throws Exception {
        funcionario.setNome("Ana Silva Santos");
        when(service.atualizar(eq(1), any(Funcionario.class))).thenReturn(funcionario);

        mockMvc.perform(put("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva Santos"));

        verify(service).atualizar(eq(1), any(Funcionario.class));
    }

    @Test
    void putFuncionarioNotFound() throws Exception {
        when(service.atualizar(eq(1), any(Funcionario.class)))
                .thenThrow(new RuntimeException("Funcionário não encontrado"));

        mockMvc.perform(put("/funcionarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(funcionario)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(Funcionario.class));
    }
}