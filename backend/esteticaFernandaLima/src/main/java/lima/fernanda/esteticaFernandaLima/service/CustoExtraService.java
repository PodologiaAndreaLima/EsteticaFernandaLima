package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustoExtraService {

    private final CustoExtraRepository repository;

    public CustoExtraService(CustoExtraRepository repository) {
        this.repository = repository;
    }

    public List<CustoExtra> listarTodos() {
        return repository.findAll();
    }

    public CustoExtra salvar(CustoExtra custoExtra) {
        return repository.save(custoExtra);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Custo Extra não encontrado");
        }
        repository.deleteById(id);
    }

    public CustoExtra atualizar(Integer id, CustoExtra custoExtraAtualizado) {
        CustoExtra custoExtraExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custo Extra não encontrado"));

        custoExtraExistente.setDescricao(custoExtraAtualizado.getDescricao());
        custoExtraExistente.setValor(custoExtraAtualizado.getValor());
        custoExtraExistente.setNome(custoExtraAtualizado.getNome());
        custoExtraExistente.setData(custoExtraAtualizado.getData());


        return repository.save(custoExtraExistente);
    }

}
