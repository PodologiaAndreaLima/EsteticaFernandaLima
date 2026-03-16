package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.FuncionarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(FuncionarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

        return repository.save(funcionarioExistente);
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
