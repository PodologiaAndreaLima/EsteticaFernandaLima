package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.ClienteMapper;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Cliente cliente;
    private ClienteResponse clienteResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Fernanda Lima");
        cliente.setCpf("88535514023");
        cliente.setTelefone("11999999999");
        cliente.setEmail("teste@email.com");
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        clienteResponse = new ClienteResponse(1, "Fernanda Lima", "88535514023", "teste@email.com", "11999999999", LocalDate.of(2000, 1, 1));
    }

    @Test
    void getCliente() throws Exception {
        when(service.buscarTodos(eq(null), eq(StatusCadastro.TODOS))).thenReturn(List.of(clienteResponse));

        mockMvc.perform(get("/cliente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Fernanda Lima"))
                .andExpect(jsonPath("$[0].email").value("teste@email.com"));

        verify(service).buscarTodos(eq(null));
    }

    @Test
    void getClienteVazio() throws Exception {
        when(service.buscarTodos(eq(null), eq(StatusCadastro.TODOS))).thenReturn(List.of());

        mockMvc.perform(get("/cliente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).buscarTodos(eq(null));
    }

    @Test
    void getClienteFiltro() throws Exception {
        when(service.buscarTodos(eq("fernanda"), eq(StatusCadastro.TODOS))).thenReturn(List.of(clienteResponse));

        mockMvc.perform(get("/cliente")
                        .param("busca", "fernanda")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCompleto").value("Fernanda Lima"));

        verify(service).buscarTodos(eq("fernanda"));
    }

    @Test
    void getClientePorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        mockMvc.perform(get("/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

    @Test
    void deleteClientePorId() throws Exception {
        when(service.inativar(1)).thenReturn(cliente);

        mockMvc.perform(delete("/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).inativar(1);
    }

    @Test
    void deleteClientePorIdNotFound() throws Exception {
        when(service.inativar(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        mockMvc.perform(delete("/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).inativar(1);
    }

    @Test
    void getClienteFiltroPorStatus() throws Exception {
        when(service.buscarTodos(eq(null), eq(StatusCadastro.ATIVO))).thenReturn(List.of(clienteResponse));

        mockMvc.perform(get("/cliente")
                        .param("status", "ATIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ativo").value(true));

        verify(service).buscarTodos(eq(null), eq(StatusCadastro.ATIVO));
    }

    @Test
    void reativarCliente() throws Exception {
        when(service.reativar(1)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(clienteResponse);

        mockMvc.perform(patch("/cliente/1/reativar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service).reativar(1);
    }

}