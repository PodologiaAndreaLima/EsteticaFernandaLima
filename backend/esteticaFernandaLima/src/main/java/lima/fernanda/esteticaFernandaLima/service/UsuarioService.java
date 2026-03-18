package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.config.GerenciadorTokenJwt;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioListarDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioMapper;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioTokenDto;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Slf4j
@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public void criar(Usuario novoUsuario) {
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        usuarioRepository.save(novoUsuario);
    }

    public UsuarioTokenDto autenticar(Usuario usuario) {

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(), usuario.getSenha()
        );

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(usuario.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email do usuário não encontrado")
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = gerenciadorTokenJwt.generateToken(authentication);

        logger.info("=== Login realizado com sucesso ===");
        logger.info("ID: {}", usuarioAutenticado.getId());
        logger.info("Email: {}", usuarioAutenticado.getEmail());
        logger.info("================================");

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    public List<UsuarioListarDto> listarTodos() {

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll();
        return usuariosEncontrados.stream().map(UsuarioMapper::of).toList();
    }

    public Usuario atualizar(Long id, UsuarioAtualizacaoDto usuarioAtualizacaoDto) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

    String cpfAnterior = usuario.getCpf();

    if (usuarioAtualizacaoDto.getNomeCompleto() != null) {
        usuario.setNomeCompleto(usuarioAtualizacaoDto.getNomeCompleto());
    }
    if (usuarioAtualizacaoDto.getCpf() != null) {
        usuario.setCpf(usuarioAtualizacaoDto.getCpf());
    }
    if (usuarioAtualizacaoDto.getTelefone() != null) {
        usuario.setTelefone(usuarioAtualizacaoDto.getTelefone());
    }
    if (usuarioAtualizacaoDto.getBio() != null) {
        usuario.setBio(usuarioAtualizacaoDto.getBio());
    }
    if (usuarioAtualizacaoDto.getServicosPrestados() != null) {
        usuario.setServicosPrestados(usuarioAtualizacaoDto.getServicosPrestados());
    }

    Usuario usuarioSalvo = usuarioRepository.save(usuario);

    try {
        Funcionario funcionario = buscarFuncionarioParaSincronizar(cpfAnterior, usuarioSalvo);

        if (funcionario != null) {
            if (usuarioAtualizacaoDto.getNomeCompleto() != null) {
                funcionario.setNome(usuarioAtualizacaoDto.getNomeCompleto());
            }
            if (usuarioAtualizacaoDto.getCpf() != null) {
                funcionario.setCPF(usuarioAtualizacaoDto.getCpf());
            }
            if (usuarioAtualizacaoDto.getTelefone() != null) {
                funcionario.setTelefone(usuarioAtualizacaoDto.getTelefone());
            }
            if (usuarioAtualizacaoDto.getBio() != null) {
                funcionario.setDescricao(usuarioAtualizacaoDto.getBio());
            }

            funcionarioRepository.save(funcionario);
            logger.info("Funcionário sincronizado com sucesso para o usuário: {}", id);
        } else {
            logger.warn("Nenhum funcionário correspondente encontrado para sincronização do usuário: {}", id);
        }
    } catch (Exception e) {
        logger.warn("Erro ao sincronizar funcionário para usuário {}: {}", id, e.getMessage());
    }

    return usuarioSalvo;
    }

    private Funcionario buscarFuncionarioParaSincronizar(String cpfAnterior, Usuario usuarioSalvo) {
        if (cpfAnterior != null && !cpfAnterior.isBlank()) {
            List<Funcionario> porCpfAnterior = funcionarioRepository.findAllByCpf(cpfAnterior);
            if (!porCpfAnterior.isEmpty()) {
                return porCpfAnterior.getFirst();
            }
        }

        if (usuarioSalvo.getCpf() != null && !usuarioSalvo.getCpf().isBlank()) {
            List<Funcionario> porCpfAtual = funcionarioRepository.findAllByCpf(usuarioSalvo.getCpf());
            if (!porCpfAtual.isEmpty()) {
                return porCpfAtual.getFirst();
            }
        }

        if (usuarioSalvo.getEmail() != null && !usuarioSalvo.getEmail().isBlank()) {
            List<Funcionario> porEmail = funcionarioRepository.findAllByEmailIgnoreCase(usuarioSalvo.getEmail());
            if (!porEmail.isEmpty()) {
                return porEmail.getFirst();
            }
        }

        return null;
    }

    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuarioRepository.delete(usuario);
        logger.info("Usuário deletado: {}", id);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void alterarSenha(Long id, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // valida senha atual
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        // atualiza para a nova senha (criptografada)
        String novaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(novaCriptografada);
        usuarioRepository.save(usuario);
    }

}
