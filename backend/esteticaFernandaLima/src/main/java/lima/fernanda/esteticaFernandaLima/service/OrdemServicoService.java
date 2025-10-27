package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lima.fernanda.esteticaFernandaLima.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemServicoService {

    @Autowired
    private ClienteRepository clienteRepository;

    public void setClienteRepository(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public OrdemServicoRepository getRepository() {
        return repository;
    }

    private final OrdemServicoRepository repository;

    public OrdemServicoService(OrdemServicoRepository repository) {
        this.repository = repository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    public OrdemServico salvar(OrdemServico ordemServico) {
        if (ordemServico.getCliente() != null && ordemServico.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(ordemServico.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            ordemServico.setCliente(cliente);
        }
        return repository.save(ordemServico);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Ordem de Serviço não encontrada");
        }
        repository.deleteById(id);
    }

    public OrdemServico atualizar(Integer id, OrdemServico ordemServicoAtualizado) {
        OrdemServico ordemServicoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada"));

        ordemServicoExistente.setDtHora(ordemServicoAtualizado.getDtHora());
        ordemServicoExistente.setValorFinal(ordemServicoAtualizado.getValorFinal());
        ordemServicoExistente.setObservacao(ordemServicoAtualizado.getObservacao());

        return repository.save(ordemServicoExistente);
    }

}
