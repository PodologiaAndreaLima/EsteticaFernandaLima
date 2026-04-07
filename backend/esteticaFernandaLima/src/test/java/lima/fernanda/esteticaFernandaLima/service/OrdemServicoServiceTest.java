package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoRequest;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsPageResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrdemServicoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicoProdutoRepository servicoProdutoRepository;

    @Mock
    private ComboRepository comboRepository;

    @Mock
    private OrdemServicoMsClient ordemServicoMsClient;

    @InjectMocks
    private OrdemServicoService service;

    private OrdemServicoMsResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        response = new OrdemServicoMsResponse(
                1,
                1,
                2,
                200.0f,
                LocalDate.now(),
                "Teste de ordem de servico",
                List.of()
        );
    }

    @Test
    void listar() {
        OrdemServicoMsPageResponse page = new OrdemServicoMsPageResponse();
        page.setContent(List.of(response));
        page.setTotalPages(1);

        when(ordemServicoMsClient.listarPaginado(0)).thenReturn(page);

        List<OrdemServico> result = service.listarTodos();

        assertEquals(1, result.size());
        assertEquals(200.0f, result.get(0).getValorFinal());
        verify(ordemServicoMsClient, times(1)).listarPaginado(0);
    }

    @Test
    void salvar() {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(2);
        request.setValorFinal(200.0f);
        request.setObservacao("Teste de ordem de servico");

        when(ordemServicoMsClient.criar(any(OrdemServicoRequest.class))).thenReturn(response);

        OrdemServico result = service.salvar(request);

        assertNotNull(result);
        assertEquals(200.0f, result.getValorFinal());
        verify(ordemServicoMsClient).criar(any(OrdemServicoRequest.class));
    }

    @Test
    void atualizar() {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(2);
        request.setValorFinal(250.0f);
        request.setObservacao("Atualizada");

        OrdemServicoMsResponse respostaAtualizada = new OrdemServicoMsResponse(
                1,
                1,
                2,
                250.0f,
                LocalDate.now(),
                "Atualizada",
                List.of()
        );

        when(ordemServicoMsClient.atualizar(anyInt(), any(OrdemServicoRequest.class))).thenReturn(respostaAtualizada);

        OrdemServico result = service.atualizar(1, request);

        assertNotNull(result);
        assertEquals(250.0f, result.getValorFinal());
        verify(ordemServicoMsClient).atualizar(anyInt(), any(OrdemServicoRequest.class));
    }

    @Test
    void deletar() {
        service.deletar(1);
        verify(ordemServicoMsClient).deletar(1);
    }
}
