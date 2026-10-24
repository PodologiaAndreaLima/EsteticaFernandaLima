package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.adapter.Adapter;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        cliente.setDataNascimento(LocalDate.of(2000, 1, 1));

        response = new ClienteResponse(1, "Fernanda Lima", "88535514023", "teste@email.com", "11999999999", LocalDate.of(2000, 1, 1));
    }

    @Test
    void buscarTodosQuandoBuscaForNull() {
        when(repository.findAll()).thenReturn(List.of(cliente));
        when(clienteAdapter.adapt(cliente)).thenReturn(response);

        List<ClienteResponse> result = service.buscarTodos(null);

        assertEquals(1, result.size());
        assertEquals("Fernanda Lima", result.get(0).getNomeCompleto());
        verify(repository).findAll();
    }

    @Test
    void buscarPorNomeQuandoBuscaNaoForNull() {
        when(repository.findByNomeCompletoContainingIgnoreCase(eq("fernanda"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cliente)));
        when(clienteAdapter.adapt(cliente)).thenReturn(response);

        List<ClienteResponse> result = service.buscarTodos("fernanda");

        assertEquals(1, result.size());
        verify(repository).findByNomeCompletoContainingIgnoreCase(eq("fernanda"), any(Pageable.class));
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

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.buscarPorId(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Cliente não encontrado", ex.getReason());
    }

    @Test
    void salvarClienteComSucesso() {
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente result = service.salvar(cliente);

        assertNotNull(result);
        verify(repository).save(cliente);
    }

    @Test
    void salvarClienteSempreAtivo() {
        cliente.setAtivo(false);
        when(repository.save(cliente)).thenReturn(cliente);

        service.salvar(cliente);

        assertTrue(cliente.getAtivo());
    }

    @Test
    void naoSalvarClienteComCpfDuplicado() {
        when(repository.existsByCpf(cliente.getCpf())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.salvar(cliente));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void naoSalvarClienteComEmailDuplicado() {
        when(repository.existsByEmailIgnoreCase(cliente.getEmail())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.salvar(cliente));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void inativarClienteSemApagarDoBanco() {
        when(repository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente result = service.inativar(1);

        assertFalse(result.getAtivo());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void deletarAgoraInativa() {
        when(repository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);

        service.deletar(1);

        assertFalse(cliente.getAtivo());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void reativarCliente() {
        cliente.setAtivo(false);
        when(repository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente result = service.reativar(1);

        assertTrue(result.getAtivo());
    }

    @Test
    void lancarExcecaoAoInativarClienteInexistente() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.inativar(1));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void filtrarClientesPorStatus() {
        Cliente inativo = new Cliente();
        inativo.setId(2);
        inativo.setAtivo(false);
        when(repository.findAll()).thenReturn(List.of(cliente, inativo));
        when(clienteAdapter.adapt(any(Cliente.class))).thenReturn(response);

        assertEquals(1, service.buscarTodos(null, StatusCadastro.ATIVO).size());
        assertEquals(1, service.buscarTodos(null, StatusCadastro.INATIVO).size());
        assertEquals(2, service.buscarTodos(null, StatusCadastro.TODOS).size());
    }

    @Test
    void naoAtualizarClienteComEmailDeOutroCliente() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setEmail("outro@email.com");

        when(repository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.existsByEmailIgnoreCaseAndIdNot("outro@email.com", 1)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(1, clienteAtualizado));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarClienteComSucesso() {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNomeCompleto("Novo Nome");
        clienteAtualizado.setCpf("11122233344");
        clienteAtualizado.setTelefone("11888888888");
        clienteAtualizado.setEmail("novo@email.com");

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

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(1, clienteAtualizado));

        assertEquals("Cliente não encontrado", ex.getReason());
    }
}
