package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioResponse;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.service.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FuncionarioControllerTest {

    @Mock
    private FuncionarioService service;

    @InjectMocks
    private FuncionarioController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Funcionario funcionario;
    private FuncionarioCriacaoDto criacaoDto;
    private FuncionarioAtualizacaoDto atualizaDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        funcionario.setNome("Ana Silva");
        funcionario.setEmail("ana@email.com");
        funcionario.setTelefone("11987654321");
        funcionario.setCPF("53428213807");
        funcionario.setSenha("$2a$10$abc");
        funcionario.setDescricao("Esteticista");

        criacaoDto = new FuncionarioCriacaoDto();
        criacaoDto.setNome("Ana Silva");
        criacaoDto.setEmail("ana@email.com");
        criacaoDto.setTelefone("11987654321");
        criacaoDto.setCPF("53428213807");
        criacaoDto.setSenha("Estetica@2026");
        criacaoDto.setDescricao("Esteticista");

        atualizaDto = new FuncionarioAtualizacaoDto();
        atualizaDto.setNome("Ana Silva Santos");
    }

    @Test
    void getFuncionarios() throws Exception {
        when(service.buscarTodos(eq(null))).thenReturn(List.of(FuncionarioResponse.fromFuncionario(funcionario)));

        mockMvc.perform(get("/funcionarios").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFuncionario").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ana Silva"))
                .andExpect(jsonPath("$[0].email").value("ana@email.com"))
                .andExpect(jsonPath("$[0].senha").doesNotExist());

        verify(service).buscarTodos(eq(null));
    }

    @Test
    void getFuncionariosVazio() throws Exception {
        when(service.buscarTodos(eq(null))).thenReturn(List.of());

        mockMvc.perform(get("/funcionarios").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).buscarTodos(eq(null));
    }

    @Test
    void getFuncionariosComFiltro() throws Exception {
        when(service.buscarTodos(eq("ana"))).thenReturn(List.of(FuncionarioResponse.fromFuncionario(funcionario)));

        mockMvc.perform(get("/funcionarios")
                        .param("busca", "ana")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana Silva"));

        verify(service).buscarTodos(eq("ana"));
    }

    @Test
    void getFuncionarioPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(funcionario);

        mockMvc.perform(get("/funcionarios/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(service).buscarPorId(1);
    }

    @Test
    void getFuncionarioPorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        mockMvc.perform(get("/funcionarios/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

    @Test
    void postFuncionario() throws Exception {
        when(service.salvar(any(FuncionarioCriacaoDto.class))).thenReturn(funcionario);

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criacaoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(service).salvar(any(FuncionarioCriacaoDto.class));
    }

    @Test
    void deleteFuncionarioPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/funcionarios/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteFuncionarioPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Funcionário não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/funcionarios/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void putFuncionario() throws Exception {
        funcionario.setNome("Ana Silva Santos");
        when(service.atualizar(eq(1), any(FuncionarioAtualizacaoDto.class))).thenReturn(funcionario);

        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFuncionario").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva Santos"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(service).atualizar(eq(1), any(FuncionarioAtualizacaoDto.class));
    }

    @Test
    void putFuncionarioNotFound() throws Exception {
        when(service.atualizar(eq(1), any(FuncionarioAtualizacaoDto.class)))
                .thenThrow(new RuntimeException("Funcionário não encontrado"));

        mockMvc.perform(put("/funcionarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizaDto)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(FuncionarioAtualizacaoDto.class));
    }
}

