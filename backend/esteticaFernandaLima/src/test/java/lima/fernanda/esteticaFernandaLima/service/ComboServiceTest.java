package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComboServiceTest {

    @Mock
    private ComboRepository repository;

    @InjectMocks
    private ComboService service;

    private Combo combo;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        combo = new Combo();
        combo.setIdCombo(1);
        combo.setNome("Combo Beleza");
        combo.setDescricao("Pacote completo de beleza");
        combo.setValorFinal(300.0f);
    }

    @Test
    void buscarTodos() {
        when(repository.findAll()).thenReturn(java.util.Arrays.asList(combo));
        java.util.List<Combo> result = service.buscarTodos();
        assertEquals(1, result.size());
        assertEquals("Combo Beleza", result.get(0).getNome());
        verify(repository).findAll();
    }

    @Test
    void buscarPorId() {
        when(repository.findById(1)).thenReturn(java.util.Optional.of(combo));
        Combo result = service.buscarPorId(1);
        assertNotNull(result);
        assertEquals("Combo Beleza", result.getNome());
        verify(repository).findById(1);
    }

    @Test
    void buscarPorIdNaoEncontrado() {
        when(repository.findById(2)).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.buscarPorId(2);
        });
        assertEquals("Combo não encontrado", exception.getMessage());
        verify(repository).findById(2);
    }

    @Test
    void salvar() {
        when(repository.save(combo)).thenReturn(combo);
        Combo result = service.salvar(combo);
        assertNotNull(result);
        assertEquals("Combo Beleza", result.getNome());
        assertEquals(300.0f, result.getValorFinal());
        verify(repository).save(combo);
    }

    @Test
    void salvarComDadosNulos() {
        Combo comboInvalido = new Combo();
        when(repository.save(comboInvalido)).thenReturn(comboInvalido);
        Combo result = service.salvar(comboInvalido);
        assertNotNull(result);
        verify(repository).save(comboInvalido);
    }


    @Test
    void deletar() {
        when(repository.existsById(1)).thenReturn(true);
        service.deletar(1);
        verify(repository).existsById(1);
        verify(repository).deleteById(1);
    }

    @Test
    void deletarComIdNaoEncontrado() {
        when(repository.existsById(99)).thenReturn(false);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.deletar(99);
        });
        assertEquals("Combo não encontrado", exception.getMessage());
        verify(repository).existsById(99);
        verify(repository, never()).deleteById(99);
    }


    @Test
    void atualizar() {
        Combo comboAtualizado = new Combo();
        comboAtualizado.setNome("Combo Premium");
        comboAtualizado.setDescricao("Pacote premium de beleza");
        comboAtualizado.setValorFinal(500.0f);

        when(repository.findById(1)).thenReturn(java.util.Optional.of(combo));
        when(repository.save(combo)).thenReturn(combo);

        Combo result = service.atualizar(1, comboAtualizado);

        assertNotNull(result);
        assertEquals("Combo Premium", result.getNome());
        assertEquals("Pacote premium de beleza", result.getDescricao());
        assertEquals(500.0f, result.getValorFinal());
        verify(repository).findById(1);
        verify(repository).save(combo);
    }

    @Test
    void atualizarComIdNaoEncontrado() {
        Combo comboAtualizado = new Combo();
        comboAtualizado.setNome("Combo Premium");
        comboAtualizado.setDescricao("Pacote premium");
        comboAtualizado.setValorFinal(500.0f);

        when(repository.findById(99)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.atualizar(99, comboAtualizado);
        });
        assertEquals("Combo não encontrado", exception.getMessage());
        verify(repository).findById(99);
        verify(repository, never()).save(any());
    }

}