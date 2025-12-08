package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoRequest;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.service.OrdemServicoService;
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

class OrdemServicoControllerTest {

    @Mock
    private OrdemServicoService service;

    @InjectMocks
    private OrdemServicoController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }

        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Fernanda Lima");
        cliente.setEmail("teste@email.com");

        ordemServico = new OrdemServico();
        ordemServico.setIdOrdemServico(1);
        ordemServico.setValorFinal(150.00f);
        ordemServico.setDtHora(LocalDate.now());
        ordemServico.setObservacao("Limpeza facial completa");
        ordemServico.setCliente(cliente);
    }

    @Test
    void getOrdemServico() throws Exception {
        List<OrdemServico> ordensServico = List.of(ordemServico);
        when(service.listarTodos()).thenReturn(ordensServico);

        mockMvc.perform(get("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrdemServico").value(1))
                .andExpect(jsonPath("$[0].valorFinal").value(150.00f))
                .andExpect(jsonPath("$[0].observacao").value("Limpeza facial completa"));

        verify(service).listarTodos();
    }

    @Test
    void getOrdemServicoVazio() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).listarTodos();
    }

    @Test
    void postOrdemServico() throws Exception {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(1);
        request.setValorFinal(150.00f);
        request.setObservacao("Limpeza facial completa");
        
        when(service.salvar(any(OrdemServicoRequest.class))).thenReturn(ordemServico);

        mockMvc.perform(post("/ordem-servico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrdemServico").value(1))
                .andExpect(jsonPath("$.valorFinal").value(150.00f));

        verify(service).salvar(any(OrdemServicoRequest.class));
    }

    @Test
    void deleteOrdemServicoPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/ordem-servico/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteOrdemServicoPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Ordem de Serviço não encontrada")).when(service).deletar(1);

        mockMvc.perform(delete("/ordem-servico/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void atualizarOrdemServico() throws Exception {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(1);
        request.setValorFinal(200.00f);
        request.setObservacao("Limpeza facial completa");
        
        ordemServico.setValorFinal(200.00f);
        when(service.atualizar(eq(1), any(OrdemServicoRequest.class))).thenReturn(ordemServico);

        mockMvc.perform(put("/ordem-servico/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdemServico").value(1))
                .andExpect(jsonPath("$.valorFinal").value(200.00f));

        verify(service).atualizar(eq(1), any(OrdemServicoRequest.class));
    }

    @Test
    void atualizarOrdemServicoNotFound() throws Exception {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(1);
        request.setValorFinal(150.00f);
        
        when(service.atualizar(eq(1), any(OrdemServicoRequest.class)))
                .thenThrow(new RuntimeException("Ordem de Serviço não encontrada"));

        mockMvc.perform(put("/ordem-servico/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(OrdemServicoRequest.class));
    }
}