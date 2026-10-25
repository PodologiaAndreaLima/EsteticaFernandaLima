package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.adapter.Adapter;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final Adapter<Cliente, ClienteResponse> clienteAdapter;


    public ClienteService(ClienteRepository repository, Adapter<Cliente, ClienteResponse> clienteAdapter) {
        this.repository = repository;
        this.clienteAdapter = clienteAdapter;
    }

    // mantido como estava (sem status = todos), caso outros pontos do sistema usem
    public List<ClienteResponse> buscarTodos(String busca) {
        return buscarTodos(busca, StatusCadastro.TODOS);
    }

    public List<ClienteResponse> buscarTodos(String busca, StatusCadastro status) {
        List<Cliente> clientes = (busca == null || busca.isBlank())
                ? repository.findAll()
                : repository.findByNomeCompletoContainingIgnoreCase(busca, Pageable.unpaged()).getContent();

        return clientes.stream()
                .filter(cliente -> atendeStatus(cliente, status))
                .map(clienteAdapter::adapt)
                .toList();
    }

    private boolean atendeStatus(Cliente cliente, StatusCadastro status) {
        boolean ativo = !Boolean.FALSE.equals(cliente.getAtivo());
        return switch (status) {
            case ATIVO -> ativo;
            case INATIVO -> !ativo;
            case TODOS -> true;
        };
    }

    public Cliente buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public Cliente salvar(Cliente cliente) {
        validarDuplicidade(cliente, null);
        cliente.setAtivo(true);
        return repository.save(cliente);
    }

    // exclusão lógica: o cliente continua no banco, então ordens de serviço e dashboards não quebram
    public Cliente inativar(Integer id) {
        return alterarStatus(id, false);
    }

    public Cliente reativar(Integer id) {
        return alterarStatus(id, true);
    }

    // mantido pelo nome antigo (DELETE /cliente/{id}): agora inativa em vez de apagar
    public void deletar(Integer id) {
        inativar(id);
    }

    private Cliente alterarStatus(Integer id, boolean ativo) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(ativo);
        return repository.save(cliente);
    }

    // não mexe em "ativo": mudar status só pelos métodos acima
    public Cliente atualizar(Integer id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        validarDuplicidade(clienteAtualizado, id);

        clienteExistente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());

        return repository.save(clienteExistente);
    }

    // devolve 409 com mensagem clara em vez do erro de constraint do MySQL
    private void validarDuplicidade(Cliente cliente, Integer idIgnorado) {
        String cpf = cliente.getCpf();
        String email = cliente.getEmail();

        boolean cpfEmUso = cpf != null && !cpf.isBlank() && (idIgnorado == null
                ? repository.existsByCpf(cpf)
                : repository.existsByCpfAndIdNot(cpf, idIgnorado));
        if (cpfEmUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente cadastrado com este CPF");
        }

        boolean emailEmUso = email != null && !email.isBlank() && (idIgnorado == null
                ? repository.existsByEmailIgnoreCase(email)
                : repository.existsByEmailIgnoreCaseAndIdNot(email, idIgnorado));
        if (emailEmUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente cadastrado com este e-mail");
        }
    }
}
