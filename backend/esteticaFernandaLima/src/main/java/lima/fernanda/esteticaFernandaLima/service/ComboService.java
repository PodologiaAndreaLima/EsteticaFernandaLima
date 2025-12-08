package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboService {

    private final ComboRepository repository;

    public ComboService(ComboRepository repository) {
        this.repository = repository;
    }

    public List<Combo> buscarTodos() {
        return repository.findAll();
    }

    public Combo buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Combo não encontrado"));
    }

    public Combo salvar(Combo combo) {
        return repository.save(combo);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Combo não encontrado");
        }
        repository.deleteById(id);
    }

    public Combo atualizar(Integer id, Combo comboAtualizado) {
        Combo comboExistente = buscarPorId(id);

        comboExistente.setNome(comboAtualizado.getNome());
        comboExistente.setDescricao(comboAtualizado.getDescricao());
        comboExistente.setValorFinal(comboAtualizado.getValorFinal());

        return repository.save(comboExistente);
    }
}
