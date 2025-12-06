package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.config.GerenciadorTokenJwt;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioListarDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioMapper;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioTokenDto;
import lima.fernanda.esteticaFernandaLima.enums.Role;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
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
        logger.info("Token: {}", token);
        logger.info("================================");

        return UsuarioMapper.of(usuarioAutenticado, token);
    }

    public List<UsuarioListarDto> listarTodos() {

        List<Usuario> usuariosEncontrados = usuarioRepository.findAll();
        return usuariosEncontrados.stream().map(UsuarioMapper::of).toList();
    }

    public Usuario atualizar(Long id, UsuarioCriacaoDto usuarioCriacaoDto) {
    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    
    // Atualizar campos
    if (usuarioCriacaoDto.getNomeCompleto() != null) {
        usuario.setNomeCompleto(usuarioCriacaoDto.getNomeCompleto());
    }
    if (usuarioCriacaoDto.getCpf() != null) {
        usuario.setCpf(usuarioCriacaoDto.getCpf());
    }
    if (usuarioCriacaoDto.getTelefone() != null) {
        usuario.setTelefone(usuarioCriacaoDto.getTelefone());
    }
    if (usuarioCriacaoDto.getBio() != null) {
        usuario.setBio(usuarioCriacaoDto.getBio());
    }
    if (usuarioCriacaoDto.getServicosPrestados() != null) {
        usuario.setServicosPrestados(usuarioCriacaoDto.getServicosPrestados());
    }
    if (usuarioCriacaoDto.getEmail() != null) {
        usuario.setEmail(usuarioCriacaoDto.getEmail());
    }
    
    // Atualizar role
    String roleStr = usuarioCriacaoDto.getRole();
    if (roleStr != null && !roleStr.isEmpty()) {
        try {
            usuario.setRole(Role.valueOf(roleStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            usuario.setRole(Role.USER);
        }
    }
    
    // Só atualiza senha se foi informada (não é obrigatória na edição)
    if (usuarioCriacaoDto.getSenha() != null && !usuarioCriacaoDto.getSenha().isEmpty()) {
        String senhaCriptografada = passwordEncoder.encode(usuarioCriacaoDto.getSenha());
        usuario.setSenha(senhaCriptografada);
    }
    
    return usuarioRepository.save(usuario);
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
