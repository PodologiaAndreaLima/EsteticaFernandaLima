package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrdemServicoServiceTest {
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OrdemServicoRepository repository;

    @InjectMocks
    private OrdemServicoService service;

    private OrdemServico ordemServico;
    private OrdemServicoResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ordemServico = new OrdemServico();
        ordemServico.setDtHora(null);
        ordemServico.setValorFinal(200.0f);
        ordemServico.setObservacao("Teste de ordem de serviço");

        response = new OrdemServicoResponse(1, 200f, null, "Teste de ordem de serviço");

        service.setClienteRepository(clienteRepository);
        }



    @Test
    void listar() {
        when(repository.findAll()).thenReturn(java.util.Arrays.asList(ordemServico));

        java.util.List<OrdemServico> result = service.listarTodos();

        assertEquals(1, result.size());
        assertEquals(200.0f, result.get(0).getValorFinal());
        verify(repository).findAll();

    }

    @Test
    void salvar() {
        when(repository.save(ordemServico)).thenReturn(ordemServico);
        OrdemServico result = service.salvar(ordemServico);

        assertNotNull(result);

        verify(repository).save(ordemServico);
    }

    @Test
    void salvarClienteNaoEncontrado() {
        // Arrange: criar um cliente com id e associar à ordem
        Cliente cliente = new Cliente();
        cliente.setId(1);
        ordemServico.setCliente(cliente);

        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.salvar(ordemServico));

        assertEquals("Cliente não encontrado", ex.getMessage());
        // o repository não deve ser chamado quando o cliente não for encontrado
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    void deletar() {
        when(repository.existsById(1)).thenReturn(true);

        service.deletar(1);

        verify(repository).deleteById(1);
    }

    @Test
    void deletarNaoEncontrado() {
        when(repository.existsById(1)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deletar(1));

        assertEquals("Ordem de Serviço não encontrada", ex.getMessage());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void atualizar() {
        OrdemServico atualizado = new OrdemServico();
        atualizado.setDtHora(null);
        atualizado.setValorFinal(250.0f);
        atualizado.setObservacao("Ordem de serviço atualizada");

        when(repository.findById(1)).thenReturn(java.util.Optional.of(ordemServico));
        when(repository.save(ordemServico)).thenReturn(ordemServico);

        OrdemServico result = service.atualizar(1, atualizado);

        assertEquals(250.0f, result.getValorFinal());
        assertEquals("Ordem de serviço atualizada", result.getObservacao());
        verify(repository).save(ordemServico);
    }

    @Test
    void atualizarNaoEncontrado() {
        when(repository.findById(1)).thenReturn(java.util.Optional.empty());

        OrdemServico atualizado = new OrdemServico();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizar(1, atualizado));

        assertEquals("Ordem de Serviço não encontrada", ex.getMessage());
    }
}
