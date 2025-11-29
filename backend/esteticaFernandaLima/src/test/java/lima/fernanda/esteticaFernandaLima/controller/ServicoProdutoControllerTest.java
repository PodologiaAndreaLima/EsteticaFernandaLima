package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.service.ServicoProdutoService;
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

class ServicoProdutoControllerTest {

    @Mock
    private ServicoProdutoService service;

    @InjectMocks
    private ServicoProdutoController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ServicoProduto servicoProduto;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
            objectMapper = new ObjectMapper();
        }

        servicoProduto = new ServicoProduto();
        servicoProduto.setIdProdutoServico(1);
        servicoProduto.setNome("Massagem Relaxante");
        servicoProduto.setProduto(false);
        servicoProduto.setDespesa(20.00f);
        servicoProduto.setValorVenda(100.00f);
        servicoProduto.setDescricao("Massagem completa");
    }

    @Test
    void getServicoProduto() throws Exception {
        List<ServicoProduto> servicosProdutos = List.of(servicoProduto);
        when(service.buscarTodos()).thenReturn(servicosProdutos);

        mockMvc.perform(get("/servico-produto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProdutoServico").value(1))
                .andExpect(jsonPath("$[0].nome").value("Massagem Relaxante"))
                .andExpect(jsonPath("$[0].valorVenda").value(100.00f));

        verify(service).buscarTodos();
    }

    @Test
    void getServicoProdutoVazio() throws Exception {
        when(service.buscarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/servico-produto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).buscarTodos();
    }

    @Test
    void getServicoProdutoPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(servicoProduto);

        mockMvc.perform(get("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProdutoServico").value(1))
                .andExpect(jsonPath("$.nome").value("Massagem Relaxante"))
                .andExpect(jsonPath("$.descricao").value("Massagem completa"));

        verify(service).buscarPorId(1);
    }

    @Test
    void getServicoProdutoPorIdNotFound() throws Exception {
        when(service.buscarPorId(1)).thenThrow(new RuntimeException("Servico/produto não encontrado"));

        mockMvc.perform(get("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).buscarPorId(1);
    }

    @Test
    void postServicoProduto() throws Exception {
        when(service.salvar(any(ServicoProduto.class))).thenReturn(servicoProduto);

        mockMvc.perform(post("/servico-produto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(servicoProduto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProdutoServico").value(1))
                .andExpect(jsonPath("$.nome").value("Massagem Relaxante"));

        verify(service).salvar(any(ServicoProduto.class));
    }

    @Test
    void deleteServicoProdutoPorId() throws Exception {
        doNothing().when(service).deletar(1);

        mockMvc.perform(delete("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deletar(1);
    }

    @Test
    void deleteServicoProdutoPorIdNotFound() throws Exception {
        doThrow(new RuntimeException("Servico/produto não encontrado")).when(service).deletar(1);

        mockMvc.perform(delete("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).deletar(1);
    }

    @Test
    void atualizarServicoProduto() throws Exception {
        servicoProduto.setNome("Massagem Relaxante Premium");
        when(service.atualizar(eq(1), any(ServicoProduto.class))).thenReturn(servicoProduto);

        mockMvc.perform(put("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(servicoProduto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProdutoServico").value(1))
                .andExpect(jsonPath("$.nome").value("Massagem Relaxante Premium"));

        verify(service).atualizar(eq(1), any(ServicoProduto.class));
    }

    @Test
    void atualizarServicoProdutoNotFound() throws Exception {
        when(service.atualizar(eq(1), any(ServicoProduto.class)))
                .thenThrow(new RuntimeException("Servico/produto não encontrado"));

        mockMvc.perform(put("/servico-produto/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(servicoProduto)))
                .andExpect(status().isNotFound());

        verify(service).atualizar(eq(1), any(ServicoProduto.class));
    }
}