package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.repository.CustoFixoRepository;
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

class CustoFixoServiceTest {

    @Mock
    private CustoFixoRepository repository;

    @InjectMocks
    private CustoFixoService service;

    private CustoFixo custoFixo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        custoFixo = new CustoFixo();
        custoFixo.setNome("Aluguel");
        custoFixo.setDescricao("Pagamento mensal");
        custoFixo.setValorMensal(2500.0F);
    }

    @Test
    void listarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(custoFixo));

        List<CustoFixo> result = service.listarTodos();

        assertEquals(1, result.size());
        assertEquals("Aluguel", result.get(0).getNome());
        verify(repository).findAll();
    }

    @Test
    void salvarComSucesso() {
        when(repository.save(custoFixo)).thenReturn(custoFixo);

        CustoFixo result = service.salvar(custoFixo);

        assertNotNull(result);
        verify(repository).save(custoFixo);
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

        assertEquals("Custo Fixo não encontrado", ex.getMessage());
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void atualizarComSucesso() {
        CustoFixo atualizado = new CustoFixo();
        atualizado.setNome("Internet");
        atualizado.setDescricao("Fibra 500mb");
        atualizado.setValorMensal(120.0F);

        when(repository.findById(1)).thenReturn(Optional.of(custoFixo));
        when(repository.save(custoFixo)).thenReturn(custoFixo);

        CustoFixo result = service.atualizar(1, atualizado);

        assertEquals("Internet", result.getNome());
        assertEquals("Fibra 500mb", result.getDescricao());
        assertEquals(120.0F, result.getValorMensal());
        verify(repository).save(custoFixo);
    }

    @Test
    void lancarExcecaoAoAtualizarInexistente() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        CustoFixo atualizado = new CustoFixo();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizar(1, atualizado));

        assertEquals("Custo Fixo não encontrado", ex.getMessage());
    }
}
