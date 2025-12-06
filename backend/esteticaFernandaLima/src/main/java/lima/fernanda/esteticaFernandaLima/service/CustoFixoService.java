package lima.fernanda.esteticaFernandaLima.service;


import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.repository.CustoFixoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustoFixoService {

    private final CustoFixoRepository repository;

    public CustoFixoService(CustoFixoRepository repository) {
        this.repository = repository;
    }

    public List<CustoFixo> listarTodos() {
        return repository.findAll();
    }

    public CustoFixo salvar(CustoFixo custoFixo) {
        return repository.save(custoFixo);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Custo Fixo não encontrado");
        }
        repository.deleteById(id);
    }

    public CustoFixo atualizar(Integer id, CustoFixo custoFixoAtualizado) {
        CustoFixo custoFixoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custo Fixo não encontrado"));

        custoFixoExistente.setDescricao(custoFixoAtualizado.getDescricao());
        custoFixoExistente.setValorMensal(custoFixoAtualizado.getValorMensal());
        custoFixoExistente.setDescricao(custoFixoAtualizado.getDescricao());
        custoFixoExistente.setNome(custoFixoAtualizado.getNome());
        custoFixoExistente.setData(custoFixoAtualizado.getData());

        return repository.save(custoFixoExistente);
    }

}
