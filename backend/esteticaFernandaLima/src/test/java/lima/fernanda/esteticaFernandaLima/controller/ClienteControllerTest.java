package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.service.ClienteService;
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

class ClienteControllerTest {

    @Mock
    private ClienteService service;

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
        List<ClienteResponse> clientes = List.of(clienteResponse);
        when(service.buscarTodos(null)).thenReturn(clientes);

        mockMvc.perform(get("/cliente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Fernanda Lima"))
                .andExpect(jsonPath("$[0].email").value("teste@email.com"));

        verify(service).buscarTodos(null);
    }

    @Test
    void getClienteVazio() throws Exception {
        when(service.buscarTodos(null)).thenReturn(List.of());

        mockMvc.perform(get("/cliente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).buscarTodos(null);
    }

    @Test
    void getClienteFiltro() throws Exception {
        List<ClienteResponse> clientes = List.of(clienteResponse);
        when(service.buscarTodos("fernanda")).thenReturn(clientes);

        mockMvc.perform(get("/cliente")
                .param("busca", "fernanda")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCompleto").value("Fernanda Lima"));

        verify(service).buscarTodos("fernanda");
    }

//    @Test
//    void getClientePorId() throws Exception {
//        when(service.buscarPorId(1)).thenReturn(cliente);
//
//        mockMvc.perform(get("/cliente/1")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.nomeCompleto").value("Fernanda Lima"))
//                .andExpect(jsonPath("$.email").value("teste@email.com"));
//
//        verify(service).buscarPorId(1);
//    }

    @Test
    void getClientePorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new RuntimeException("Cliente não encontrado"));

        mockMvc.perform(get("/cliente/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

//    @Test
//    void postCliente() throws Exception {
//        when(service.salvar(any(Cliente.class))).thenReturn(cliente);
//
//        mockMvc.perform(post("/cliente")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(cliente)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.nomeCompleto").value("Fernanda Lima"));
//
//        verify(service).salvar(any(Cliente.class));
//    }

    @Test
    void deleteClientePorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/cliente/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteClientePorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Cliente não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/cliente/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

//    @Test
//    void putCliente() throws Exception {
//        cliente.setNomeCompleto("Fernanda Lima Atualizado");
//        when(service.atualizar(eq(1), any(Cliente.class))).thenReturn(cliente);
//
//        mockMvc.perform(put("/cliente/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(cliente)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.nomeCompleto").value("Fernanda Lima Atualizado"));
//
//        verify(service).atualizar(eq(1), any(Cliente.class));
//    }

}