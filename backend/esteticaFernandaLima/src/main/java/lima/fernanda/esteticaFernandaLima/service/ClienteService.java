package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.adapter.Adapter;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final Adapter<Cliente, ClienteResponse> clienteAdapter;


    public ClienteService(ClienteRepository repository, Adapter<Cliente, ClienteResponse> clienteAdapter) {
        this.repository = repository;
        this.clienteAdapter = clienteAdapter;
    }

    public List<ClienteResponse> buscarTodos(String busca) {
        if (busca == null) {
            return repository.findAll()
                    .stream()
                    .map(clienteAdapter::adapt)
                    .collect(Collectors.toList());
        }
        return repository.findByNomeCompletoContainingIgnoreCase(busca)
                .stream()
                .map(clienteAdapter::adapt)
                .collect(Collectors.toList());
    }

    public Cliente buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        repository.deleteById(id);
    }

    public Cliente atualizar(Integer id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        clienteExistente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());

        return repository.save(clienteExistente);
    }
}

