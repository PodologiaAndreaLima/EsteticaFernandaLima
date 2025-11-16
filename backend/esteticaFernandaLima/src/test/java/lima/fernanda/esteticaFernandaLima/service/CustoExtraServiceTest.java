package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustoExtraServiceTest {

    @Mock
    private CustoExtraRepository repository;

    @InjectMocks
    private CustoExtraService service;

    private CustoExtra custoExtra;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        custoExtra = new CustoExtra();
        custoExtra.setIdCustoExtra(1);
        custoExtra.setNome("Compra de materiais");
        custoExtra.setDescricao("Itens avulsos");
        custoExtra.setValor(150.0f);
        custoExtra.setData(LocalDate.of(2025, 1, 10));
    }

    @Test
    void listarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(custoExtra));

        List<CustoExtra> result = service.listarTodos();

        assertEquals(1, result.size());
        assertEquals("Compra de materiais", result.get(0).getNome());
        verify(repository).findAll();
    }

    @Test
    void salvarComSucesso() {
        when(repository.save(custoExtra)).thenReturn(custoExtra);

        CustoExtra result = service.salvar(custoExtra);

        assertNotNull(result);
        verify(repository).save(custoExtra);
    }

    @Test
    void deletarQuandoExiste() {
        when(repository.existsById(1)).thenReturn(true);

        service.deletar(1);

        verify(repository).deleteById(1);
    }

    @Test
    void lancarExcecaoAoDeletarInexistente() {
        when(repository.existsById(1)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deletar(1));

        assertEquals("Custo Extra não encontrado", ex.getMessage());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void atualizarComSucesso() {
        CustoExtra atualizado = new CustoExtra();
        atualizado.setNome("Nova compra");
        atualizado.setDescricao("Compra emergencial");
        atualizado.setValor(300.0f);
        atualizado.setData(LocalDate.of(2025, 2, 1));

        when(repository.findById(1)).thenReturn(Optional.of(custoExtra));
        when(repository.save(custoExtra)).thenReturn(custoExtra);

        CustoExtra result = service.atualizar(1, atualizado);

        assertEquals("Nova compra", result.getNome());
        assertEquals("Compra emergencial", result.getDescricao());
        assertEquals(300.0f, result.getValor());
        assertEquals(LocalDate.of(2025, 2, 1), result.getData());
        verify(repository).save(custoExtra);
    }

    @Test
    void lancarExcecaoAoAtualizarInexistente() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        CustoExtra atualizado = new CustoExtra();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizar(1, atualizado));

        assertEquals("Custo Extra não encontrado", ex.getMessage());
    }
}
