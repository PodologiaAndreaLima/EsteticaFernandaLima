package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.config.GerenciadorTokenJwt;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizadoDto;
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
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

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

    // ---------- perfil: e-mail e foto ----------

    private UsuarioAtualizacaoDto dtoComEmail(String email) {
        UsuarioAtualizacaoDto dto = new UsuarioAtualizacaoDto();
        dto.setEmail(email);
        return dto;
    }

    @Test
    void atualizarDeveAlterarEmailESincronizarComFuncionario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nova@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcionarioRepository.findAllByCpf("53428213807")).thenReturn(List.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario salvo = service.atualizar(1L, dtoComEmail("  Nova@Email.com "));

        assertEquals("nova@email.com", salvo.getEmail());
        ArgumentCaptor<Funcionario> funcionarioCaptor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioRepository).save(funcionarioCaptor.capture());
        assertEquals("nova@email.com", funcionarioCaptor.getValue().getEmail());
    }

    @Test
    void atualizarDeveRecusarEmailDeOutroUsuario() {
        Usuario outro = new Usuario();
        outro.setId(2L);
        outro.setEmail("ocupado@email.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("ocupado@email.com")).thenReturn(Optional.of(outro));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(1L, dtoComEmail("ocupado@email.com")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarComTokenDeveGerarNovoTokenQuandoProprioUsuarioTrocaEmail() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nova@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(gerenciadorTokenJwt.generateToken(any())).thenReturn("token-novo");

        UsuarioAtualizadoDto resposta = service.atualizarComToken(1L, dtoComEmail("nova@email.com"), true);

        assertEquals("nova@email.com", resposta.getEmail());
        assertEquals("token-novo", resposta.getToken());
    }

    @Test
    void atualizarComTokenNaoDeveGerarTokenParaOutroUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nova@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioAtualizadoDto resposta = service.atualizarComToken(1L, dtoComEmail("nova@email.com"), false);

        assertNull(resposta.getToken());
        verify(gerenciadorTokenJwt, never()).generateToken(any());
    }

    @Test
    void atualizarDeveSalvarERemoverFotoDePerfil() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioAtualizacaoDto comFoto = new UsuarioAtualizacaoDto();
        comFoto.setFotoPerfil("data:image/jpeg;base64,/9j/4AAQSkZJRg==");
        assertEquals("data:image/jpeg;base64,/9j/4AAQSkZJRg==", service.atualizar(1L, comFoto).getFotoPerfil());

        UsuarioAtualizacaoDto semFoto = new UsuarioAtualizacaoDto();
        semFoto.setFotoPerfil("");
        assertNull(service.atualizar(1L, semFoto).getFotoPerfil());
    }

    @Test
    void atualizarDeveRecusarFotoQueNaoEhImagem() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioAtualizacaoDto dto = new UsuarioAtualizacaoDto();
        dto.setFotoPerfil("data:text/html;base64,PHNjcmlwdD4=");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.atualizar(1L, dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void atualizarComTokenDeveResponderMesmoSeSincronizacaoDoFuncionarioFalhar() {
        usuario.setServicosPrestados(new java.util.ArrayList<>(List.of("Limpeza de pele")));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("nova@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcionarioRepository.findAllByCpf("53428213807")).thenReturn(List.of(funcionario));
        when(funcionarioRepository.save(any(Funcionario.class))).thenThrow(new RuntimeException("CPF invalido"));
        when(gerenciadorTokenJwt.generateToken(any())).thenReturn("token-novo");

        UsuarioAtualizadoDto resposta = service.atualizarComToken(1L, dtoComEmail("nova@email.com"), true);

        assertEquals("nova@email.com", resposta.getEmail());
        assertEquals("token-novo", resposta.getToken());
        assertEquals(List.of("Limpeza de pele"), resposta.getServicosPrestados());
        // o DTO tem a própria lista, desligada da coleção da entidade
        org.junit.jupiter.api.Assertions.assertNotSame(usuario.getServicosPrestados(), resposta.getServicosPrestados());
    }
}
