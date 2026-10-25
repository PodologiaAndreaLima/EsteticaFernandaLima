package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class ServicoProdutoService {

    private final ServicoProdutoRepository repository;

    public ServicoProdutoService(ServicoProdutoRepository repository) {
        this.repository = repository;
    }

    // mantido como estava, caso outros pontos do sistema (combos, vendas) usem
    public List<ServicoProduto> buscarTodos() {
        return repository.findAll();
    }

    public List<ServicoProduto> buscarPorStatus(StatusCadastro status) {
        return switch (status) {
            case ATIVO -> repository.findByAtivo(true);
            case INATIVO -> repository.findByAtivo(false);
            case TODOS -> repository.findAll();
        };
    }

    public ServicoProduto buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço/produto não encontrado"));
    }

    public ServicoProduto salvar(ServicoProduto servicoProduto) {
        servicoProduto.setAtivo(true);
        if (servicoProduto.getEstoque() == null) {
            servicoProduto.setEstoque(0); // cadastro sem estoque (serviços, versão web) começa em 0
        }
        return repository.save(servicoProduto);
    }

    // exclusão lógica: o registro continua no banco, então vendas e dashboards não quebram
    public ServicoProduto inativar(Integer id) {
        return alterarStatus(id, false);
    }

    /**
     * Compatibilidade com a API legada. A exclusão física continua disponível
     * apenas para consumidores antigos; o controller atual usa inativar().
     */
    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Servico/produto não encontrado");
        }
        repository.deleteById(id);
    }

    public ServicoProduto reativar(Integer id) {
        return alterarStatus(id, true);
    }

    private ServicoProduto alterarStatus(Integer id, boolean ativo) {
        ServicoProduto servicoProduto = buscarPorId(id);
        servicoProduto.setAtivo(ativo);
        return repository.save(servicoProduto);
    }

    // não mexe em "ativo": mudar status só pelos métodos acima
    public ServicoProduto atualizar(Integer id, ServicoProduto servicoProdutoAtualizado) {
        ServicoProduto servicoProdutoExistente = buscarPorId(id);

        servicoProdutoExistente.setNome(servicoProdutoAtualizado.getNome());
        servicoProdutoExistente.setProduto(servicoProdutoAtualizado.getProduto());
        servicoProdutoExistente.setDescricao(servicoProdutoAtualizado.getDescricao());
        servicoProdutoExistente.setDespesa(servicoProdutoAtualizado.getDespesa());
        servicoProdutoExistente.setValorVenda(servicoProdutoAtualizado.getValorVenda());
        servicoProdutoExistente.setMarca(servicoProdutoAtualizado.getMarca());
        servicoProdutoExistente.setCategoria(servicoProdutoAtualizado.getCategoria());

        // só altera se vier no body: o PUT de serviços e o da versão web não enviam esse campo
        if (servicoProdutoAtualizado.getEstoque() != null) {
            servicoProdutoExistente.setEstoque(servicoProdutoAtualizado.getEstoque());
        }

        return repository.save(servicoProdutoExistente);
    }
}