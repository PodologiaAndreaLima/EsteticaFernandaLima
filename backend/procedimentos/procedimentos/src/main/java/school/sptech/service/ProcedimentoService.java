package school.sptech.service;

import org.springframework.stereotype.Service;
import school.sptech.model.Procedimento;
import school.sptech.repository.ProcedimentoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedimentoService {

    private final ProcedimentoRepository repository;

    public ProcedimentoService(ProcedimentoRepository repository) {
        this.repository = repository;
    }

    public List<Procedimento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Procedimento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Procedimento salvar(Procedimento procedimento) {
        return repository.save(procedimento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}