package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.config.GerenciadorTokenJwt;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizadoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioDetalhesDto;
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

import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private static final Pattern SENHA_FORTE_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

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
    String emailAnterior = usuario.getEmail();

    // localiza o funcionario ainda com os dados antigos (cpf/e-mail podem mudar abaixo)
    Funcionario funcionarioVinculado = buscarFuncionarioParaSincronizar(cpfAnterior, usuario);

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

    boolean emailAlterado = false;
    if (usuarioAtualizacaoDto.getEmail() != null && !usuarioAtualizacaoDto.getEmail().isBlank()) {
        String novoEmail = usuarioAtualizacaoDto.getEmail().trim().toLowerCase();
        if (!novoEmail.equalsIgnoreCase(emailAnterior)) {
            usuarioRepository.findByEmail(novoEmail)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso por outro usuário");
                });
            usuario.setEmail(novoEmail);
            emailAlterado = true;
        }
    }

    if (usuarioAtualizacaoDto.getFotoPerfil() != null) {
        String foto = usuarioAtualizacaoDto.getFotoPerfil().trim();
        if (foto.isEmpty()) {
            usuario.setFotoPerfil(null); // remove a foto
        } else {
            validarFotoPerfil(foto);
            usuario.setFotoPerfil(foto);
        }
    }

    Usuario usuarioSalvo = usuarioRepository.save(usuario);

    try {
        Funcionario funcionario = funcionarioVinculado != null
            ? funcionarioVinculado
            : buscarFuncionarioParaSincronizar(cpfAnterior, usuarioSalvo);

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
            if (emailAlterado) {
                funcionario.setEmail(usuarioSalvo.getEmail());
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

    /**
     * Atualiza o usuario e, se for o proprio usuario logado trocando o e-mail,
     * devolve um novo token (o token antigo tem o e-mail anterior como subject).
     */
    public UsuarioAtualizadoDto atualizarComToken(Long id, UsuarioAtualizacaoDto dto, boolean ehProprioUsuario) {
        String emailAnterior = usuarioRepository.findById(id).map(Usuario::getEmail).orElse(null);
        Usuario usuarioSalvo = atualizar(id, dto);

        boolean emailMudou = emailAnterior != null && !emailAnterior.equalsIgnoreCase(usuarioSalvo.getEmail());
        String novoToken = (emailMudou && ehProprioUsuario) ? gerarToken(usuarioSalvo) : null;
        return UsuarioMapper.of(usuarioSalvo, novoToken, novoToken != null);
    }

    private String gerarToken(Usuario usuario) {
        UsuarioDetalhesDto detalhes = new UsuarioDetalhesDto(usuario);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                detalhes.getUsername(), null, detalhes.getAuthorities()
        );
        return gerenciadorTokenJwt.generateToken(authentication);
    }

    private void validarFotoPerfil(String foto) {
        int virgula = foto.indexOf(',');
        String cabecalho = virgula > 0 ? foto.substring(0, virgula) : "";
        boolean formatoValido = cabecalho.equals("data:image/jpeg;base64")
                || cabecalho.equals("data:image/png;base64")
                || cabecalho.equals("data:image/webp;base64");
        if (!formatoValido) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto de perfil deve ser uma imagem JPEG, PNG ou WEBP");
        }
        try {
            Base64.getDecoder().decode(foto.substring(virgula + 1));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto de perfil inválida");
        }
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

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }

        validarForcaSenha(novaSenha);

        String novaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(novaCriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        sincronizarSenhaFuncionario(usuarioSalvo, novaCriptografada);
    }

    public void resetarSenhaAdmin(Long id, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        validarForcaSenha(novaSenha);

        String novaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(novaCriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        sincronizarSenhaFuncionario(usuarioSalvo, novaCriptografada);
    }

    private void validarForcaSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha não pode ser vazia");
        }

        if (novaSenha.length() < 8 || novaSenha.length() > 20 || !SENHA_FORTE_PATTERN.matcher(novaSenha).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Senha deve ter no minimo 8 caracteres, com letra maiuscula, minuscula, numero e caractere especial"
            );
        }
    }

    private void sincronizarSenhaFuncionario(Usuario usuarioSalvo, String senhaCriptografada) {
        try {
            Funcionario funcionario = buscarFuncionarioParaSincronizar(usuarioSalvo.getCpf(), usuarioSalvo);
            if (funcionario == null) {
                logger.warn("Nenhum funcionário correspondente encontrado para sincronização de senha do usuário: {}", usuarioSalvo.getId());
                return;
            }

            funcionario.setSenha(senhaCriptografada);
            funcionarioRepository.save(funcionario);
            logger.info("Senha sincronizada no funcionário para o usuário: {}", usuarioSalvo.getId());
        } catch (Exception e) {
            logger.warn("Erro ao sincronizar senha no funcionário para usuário {}: {}", usuarioSalvo.getId(), e.getMessage());
        }
    }

}
