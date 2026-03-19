package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.FuncionarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public FuncionarioService(
            FuncionarioRepository repository,
            PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Funcionario> buscarTodos() {
        return repository.findAll();
    }

    public Funcionario buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public Funcionario salvar(FuncionarioCriacaoDto dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setCPF(dto.getCPF());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setEmail(dto.getEmail());
        funcionario.setDescricao(dto.getDescricao());
        funcionario.setSenha(prepararSenha(dto.getSenha()));

        return repository.save(funcionario);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Funcionário não encontrado");
        }
        repository.deleteById(id);
    }

    public Funcionario atualizar(Integer id, FuncionarioAtualizacaoDto dto) {
        Funcionario funcionarioExistente = buscarPorId(id);

        if (dto.getNome() != null) {
            funcionarioExistente.setNome(dto.getNome());
        }
        if (dto.getCPF() != null) {
            funcionarioExistente.setCPF(dto.getCPF());
        }
        if (dto.getTelefone() != null) {
            funcionarioExistente.setTelefone(dto.getTelefone());
        }
        if (dto.getEmail() != null) {
            funcionarioExistente.setEmail(dto.getEmail());
        }
        if (dto.getDescricao() != null) {
            funcionarioExistente.setDescricao(dto.getDescricao());
        }
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            funcionarioExistente.setSenha(prepararSenha(dto.getSenha()));
        }

        if (funcionarioExistente.getUsuario() != null) {
            Usuario usuario = funcionarioExistente.getUsuario();

            if (dto.getNome() != null) {
                usuario.setNomeCompleto(dto.getNome());
            }
            if (dto.getCPF() != null) {
                usuario.setCpf(dto.getCPF());
            }
            if (dto.getTelefone() != null) {
                usuario.setTelefone(dto.getTelefone());
            }
            if (dto.getEmail() != null) {
                usuario.setEmail(dto.getEmail());
            }
            if (dto.getDescricao() != null) {
                usuario.setBio(dto.getDescricao());
            }

            usuarioRepository.save(usuario);
            log.info("Usuario sincronizado com sucesso para o funcionario: {}", id);
        }

        return repository.save(funcionarioExistente);
    }

    public void alterarSenha(Integer id, String novaSenha) {
        Funcionario funcionario = buscarPorId(id);

        validarForcaSenha(novaSenha);
        String senhaEncriptada = passwordEncoder.encode(novaSenha);
        funcionario.setSenha(senhaEncriptada);
        repository.save(funcionario);

        if (funcionario.getUsuario() != null) {
            Usuario usuario = funcionario.getUsuario();
            usuario.setSenha(senhaEncriptada);
            usuarioRepository.save(usuario);
            log.info("Senha do usuario sincronizada com sucesso para o funcionario: {}", id);
        }
    }

    private void validarForcaSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Nova senha nao pode ser vazia");
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
        if (!senha.matches(regex)) {
            throw new RuntimeException("Senha deve conter ao menos 8 caracteres, incluindo maiuscula, minuscula, numero e simbolo");
        }
    }

    private String prepararSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RuntimeException("Senha não pode ser vazia");
        }

        if (senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$")) {
            return senha;
        }

        return passwordEncoder.encode(senha);
    }
}
