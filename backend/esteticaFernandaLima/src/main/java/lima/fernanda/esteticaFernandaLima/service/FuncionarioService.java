package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public List<Funcionario> buscarTodos() {
            return repository.findAll();
    }

    public Funcionario buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
    }

    public Funcionario salvar(Funcionario funcionario) {
        return repository.save(funcionario);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Funcionário não encontrado");
        }
        repository.deleteById(id);
    }

    public Funcionario atualizar(Integer id, Funcionario funcionarioAtualizado) {
        Funcionario funcionarioExistente = buscarPorId(id);

        funcionarioExistente.setNome(funcionarioAtualizado.getNome());
        funcionarioExistente.setCPF(funcionarioAtualizado.getCPF());
        funcionarioExistente.setTelefone(funcionarioAtualizado.getTelefone());
        funcionarioExistente.setEmail(funcionarioAtualizado.getEmail());
        funcionarioExistente.setSenha(funcionarioAtualizado.getSenha());
        funcionarioExistente.setDescricao(funcionarioAtualizado.getDescricao());

        return repository.save(funcionarioExistente);
    }

}

