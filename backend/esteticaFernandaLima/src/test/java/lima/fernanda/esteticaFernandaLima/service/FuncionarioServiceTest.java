package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.FuncionarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FuncionarioService service;

    private FuncionarioCriacaoDto criacaoDto;
    private Funcionario funcionarioExistente;

    @BeforeEach
    void setUp() {
        criacaoDto = new FuncionarioCriacaoDto();
        criacaoDto.setNome("Ana Silva");
        criacaoDto.setCPF("53428213807");
        criacaoDto.setTelefone("11987654321");
        criacaoDto.setEmail("ana@email.com");
        criacaoDto.setDescricao("Esteticista");
        criacaoDto.setSenha("Estetica@2026");

        funcionarioExistente = new Funcionario();
        funcionarioExistente.setIdFuncionario(1);
        funcionarioExistente.setNome("Ana Silva");
        funcionarioExistente.setCPF("53428213807");
        funcionarioExistente.setTelefone("11987654321");
        funcionarioExistente.setEmail("ana@email.com");
        funcionarioExistente.setDescricao("Esteticista");
        funcionarioExistente.setSenha("$2a$10$hashAntigo");
    }

    @Test
    void salvarDeveHashSenhaQuandoRecebeTextoPlano() {
        when(passwordEncoder.encode("Estetica@2026")).thenReturn("$2a$10$hashNovo");
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario salvo = service.salvar(criacaoDto);

        assertEquals("$2a$10$hashNovo", salvo.getSenha());
        assertNotEquals("Estetica@2026", salvo.getSenha());
    }

    @Test
    void atualizarSemSenhaNaoAlteraHashExistente() {
        FuncionarioAtualizacaoDto dto = new FuncionarioAtualizacaoDto();
        dto.setNome("Ana Silva Santos");

        when(repository.findById(1)).thenReturn(Optional.of(funcionarioExistente));
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario atualizado = service.atualizar(1, dto);

        assertEquals("Ana Silva Santos", atualizado.getNome());
        assertEquals("$2a$10$hashAntigo", atualizado.getSenha());
        verify(passwordEncoder, org.mockito.Mockito.never()).encode(any(String.class));
    }

    @Test
    void atualizarComSenhaDeveHashSenhaNova() {
        FuncionarioAtualizacaoDto dto = new FuncionarioAtualizacaoDto();
        dto.setSenha("NovaSenha@2026");

        when(repository.findById(1)).thenReturn(Optional.of(funcionarioExistente));
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("$2a$10$hashNovo");
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario atualizado = service.atualizar(1, dto);

        assertEquals("$2a$10$hashNovo", atualizado.getSenha());
    }

    @Test
    void salvarNaoRehashQuandoSenhaJaEstaEmBcrypt() {
        criacaoDto.setSenha("$2a$10$jaHash");
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario salvo = service.salvar(criacaoDto);

        assertTrue(salvo.getSenha().startsWith("$2a$"));
        verify(passwordEncoder, org.mockito.Mockito.never()).encode(any(String.class));

        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);
        verify(repository).save(captor.capture());
        assertEquals("$2a$10$jaHash", captor.getValue().getSenha());
    }
}

