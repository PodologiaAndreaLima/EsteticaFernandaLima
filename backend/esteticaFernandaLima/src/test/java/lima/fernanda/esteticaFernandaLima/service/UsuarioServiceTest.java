package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;
    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCpf("53428213807");
        usuario.setEmail("ana@email.com");
        usuario.setSenha("$2a$10$senhaAtualHash");

        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        funcionario.setCPF("53428213807");
        funcionario.setEmail("ana@email.com");
        funcionario.setSenha("$2a$10$hashAntigo");
    }

    @Test
    void resetarSenhaAdminDeveCriptografarESincronizarComFuncionario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("$2a$10$hashNovo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcionarioRepository.findAllByCpf("53428213807")).thenReturn(List.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.resetarSenhaAdmin(1L, "NovaSenha@2026");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("$2a$10$hashNovo", usuarioCaptor.getValue().getSenha());

        ArgumentCaptor<Funcionario> funcionarioCaptor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(funcionarioCaptor.capture());
        assertEquals("$2a$10$hashNovo", funcionarioCaptor.getValue().getSenha());
    }

    @Test
    void resetarSenhaAdminDeveRetornarBadRequestQuandoSenhaFraca() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.resetarSenhaAdmin(1L, "abc")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void alterarSenhaDeveRetornarBadRequestQuandoSenhaAtualIncorreta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaErrada@2026", "$2a$10$senhaAtualHash")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.alterarSenha(1L, "SenhaErrada@2026", "NovaSenha@2026")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void alterarSenhaDeveAtualizarUsuarioESincronizarFuncionario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Atual@2026", "$2a$10$senhaAtualHash")).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("$2a$10$hashNovo");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcionarioRepository.findAllByCpf("53428213807")).thenReturn(List.of(funcionario));

        service.alterarSenha(1L, "Atual@2026", "NovaSenha@2026");

        verify(usuarioRepository).save(any(Usuario.class));
        verify(funcionarioRepository).save(any(Funcionario.class));
    }
}

