package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.adapter.Adapter;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private Adapter<Cliente, ClienteResponse> clienteAdapter;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente;
    private ClienteResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNomeCompleto("Fernanda Lima");
        cliente.setCpf("88535514023");
        cliente.setTelefone("11999999999");
        cliente.setEmail("teste@email.com");
        cliente.setSenha("123");

        response = new ClienteResponse(1, "Fernanda Lima", "teste@email.com");
    }

    @Test
    void buscarTodosQuandoBuscaForNull() {
        when(repository.findAll()).thenReturn(Arrays.asList(cliente));
        when(clienteAdapter.adapt(cliente)).thenReturn(response);

        List<ClienteResponse> result = service.buscarTodos(null);

        assertEquals(1, result.size());
        assertEquals("Fernanda Lima", result.get(0).getNomeCompleto());
        verify(repository).findAll();
    }

    @Test
    void buscarPorNomeQuandoBuscaNaoForNull() {
        when(repository.findByNomeCompletoContainingIgnoreCase("fernanda"))
                .thenReturn(Arrays.asList(cliente));
        when(clienteAdapter.adapt(cliente)).thenReturn(response);

        List<ClienteResponse> result = service.buscarTodos("fernanda");

        assertEquals(1, result.size());
        verify(repository).findByNomeCompletoContainingIgnoreCase("fernanda");
    }

    @Test
    void retornarListaVaziaQuandoNaoEncontrarClientes() {
        when(repository.findAll()).thenReturn(List.of());

        List<ClienteResponse> result = service.buscarTodos(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void buscarClientePorId() {
        when(repository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente result = service.buscarPorId(1);

        assertNotNull(result);
        assertEquals("Fernanda Lima", result.getNomeCompleto());
    }

    @Test
    void lancarExcecaoQuandoClienteNaoEncontrado() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.buscarPorId(1));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }

    @Test
    void salvarClienteComSucesso() {
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente result = service.salvar(cliente);

        assertNotNull(result);
        verify(repository).save(cliente);
    }

    @Test
    void deletarClienteQuandoExiste() {
        when(repository.existsById(1)).thenReturn(true);

        service.deletar(1);

        verify(repository).deleteById(1);
    }

    @Test
    void lancarExcecaoAoDeletarClienteInexistente() {
        when(repository.existsById(1)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deletar(1));

        assertEquals("Cliente não encontrado", ex.getMessage());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void atualizarClienteComSucesso() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNomeCompleto("Novo Nome");
        clienteAtualizado.setCpf("11122233344");
        clienteAtualizado.setTelefone("11888888888");
        clienteAtualizado.setEmail("novo@email.com");
        clienteAtualizado.setSenha("novaSenha");

        when(repository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente result = service.atualizar(1, clienteAtualizado);

        assertEquals("Novo Nome", result.getNomeCompleto());
        assertEquals("11122233344", result.getCpf());
        verify(repository).save(cliente);
    }

    @Test
    void lancarExcecaoAoAtualizarClienteInexistente() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Cliente clienteAtualizado = new Cliente();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizar(1, clienteAtualizado));

        assertEquals("Cliente não encontrado", ex.getMessage());
    }
}
