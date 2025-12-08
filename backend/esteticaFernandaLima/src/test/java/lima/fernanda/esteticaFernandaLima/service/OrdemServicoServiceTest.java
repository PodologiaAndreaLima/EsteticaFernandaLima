package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoRequest;
import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.OrdemServicoRepository;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

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
        Cliente cliente = new Cliente();
        cliente.setId(1);
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(1);
        request.setValorFinal(200.0f);
        request.setObservacao("Teste de ordem de serviço");
        
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(OrdemServico.class))).thenReturn(ordemServico);
        
        OrdemServico result = service.salvar(request);

        assertNotNull(result);
        verify(repository).save(any(OrdemServico.class));
    }

    @Test
    void salvarClienteNaoEncontrado() {
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setUsuarioId(1);
        request.setValorFinal(200.0f);

        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.salvar(request));

        assertEquals("Cliente não encontrado", ex.getMessage());
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
        Cliente cliente = new Cliente();
        cliente.setId(1);
        
        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);
        request.setValorFinal(250.0f);
        request.setObservacao("Ordem de serviço atualizada");

        when(repository.findById(1)).thenReturn(java.util.Optional.of(ordemServico));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(repository.save(any(OrdemServico.class))).thenReturn(ordemServico);

        OrdemServico result = service.atualizar(1, request);

        assertNotNull(result);
        verify(repository).save(any(OrdemServico.class));
    }

    @Test
    void atualizarNaoEncontrado() {
        when(repository.findById(1)).thenReturn(java.util.Optional.empty());

        OrdemServicoRequest request = new OrdemServicoRequest();
        request.setClienteId(1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizar(1, request));

        assertEquals("Ordem não encontrada", ex.getMessage());
    }
}
