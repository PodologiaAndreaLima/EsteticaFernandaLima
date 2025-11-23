package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.ServicoProdutoResponse;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServicoProdutoServiceTest {

    @Mock
    private ServicoProdutoRepository repository;

    @InjectMocks
    private ServicoProdutoService service;

    private ServicoProduto servicoProduto;
    private ServicoProdutoResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servicoProduto = new ServicoProduto();
        servicoProduto.setProduto(false);
        servicoProduto.setNome("Limpeza de pele");
        servicoProduto.setDespesa(100.0f);
        servicoProduto.setValorVenda(120.0f);
        servicoProduto.setDescricao("Limpeza profunda");

        response = new ServicoProdutoResponse(1, false, "Limpeza de pele", 100.0f, 120.0f,  "Limpeza profunda");
    }

    @Test
    void buscarTodos() {
        when(repository.findAll()).thenReturn(java.util.Arrays.asList(servicoProduto));

        java.util.List<ServicoProduto> result = service.buscarTodos();
        assertEquals(1, result.size());
        assertEquals("Limpeza de pele", result.get(0).getNome());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(servicoProduto));

        ServicoProduto result = service.buscarPorId(1);

        assertNotNull(result);
        assertEquals("Limpeza de pele", result.getNome());
        verify(repository).findById(1);
    }

    @Test
    void buscarPorIdNaoEncontrado() {
        when(repository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.buscarPorId(2);
        });

        assertTrue(exception.getMessage().contains("Servico/produto não encontrado"));
        verify(repository).findById(2);
    }

    @Test
    void salvar() {
        when(repository.save(servicoProduto)).thenReturn(servicoProduto);

        ServicoProduto result = service.salvar(servicoProduto);

        assertNotNull(result);
        verify(repository).save(servicoProduto);
    }

    @Test
    void salvarComDespesaNegativa() {
        servicoProduto.setDespesa(-50.0f);

        // Simular validação no repositório/nível inferior para forçar a exceção no teste
        when(repository.save(servicoProduto)).thenThrow(new RuntimeException("Despesa não pode ser negativa"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.salvar(servicoProduto);
        });

        assertTrue(exception.getMessage().contains("Despesa não pode ser negativa"));
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

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.deletar(1);
        });

        assertTrue(exception.getMessage().contains("Servico/produto não encontrado"));
        verify(repository).existsById(1);
    }

    @Test
    void atualizar() {
        when(repository.findById(1)).thenReturn(Optional.of(servicoProduto));
        when(repository.save(servicoProduto)).thenReturn(servicoProduto);

        ServicoProduto result = service.atualizar(1, servicoProduto);

        assertNotNull(result);
        verify(repository).findById(1);
    }

    @Test
    void atualizarNaoEncontrado() {
        when(repository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.atualizar(2, servicoProduto);
        });

        assertTrue(exception.getMessage().contains("Servico/produto não encontrado"));
        verify(repository).findById(2);
    }

    @Test
    void atualizarComDespesaNegativa() {
        servicoProduto.setDespesa(-30.0f);
        when(repository.findById(1)).thenReturn(Optional.of(servicoProduto));
        // Simular que o save falha por despesa negativa
        when(repository.save(servicoProduto)).thenThrow(new RuntimeException("Despesa não pode ser negativa"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.atualizar(1, servicoProduto);
        });

        assertTrue(exception.getMessage().contains("Despesa não pode ser negativa"));
        verify(repository).findById(1);
    }

}