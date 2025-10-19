package lima.fernanda.esteticaFernandaLima.service;



import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoProdutoService {

    private final ServicoProdutoRepository repository;

    public ServicoProdutoService(ServicoProdutoRepository repository) {
        this.repository = repository;
    }

    public List<ServicoProduto> buscarTodos() {
        return repository.findAll();
    }

    public ServicoProduto buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servico/produto não encontrado"));
    }

    public ServicoProduto salvar(ServicoProduto servicoProduto) {
        return repository.save(servicoProduto);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Servico/produto não encontrado");
        }
        repository.deleteById(id);
    }

    public ServicoProduto atualizar(Integer id, ServicoProduto servicoProdutoAtualizado) {
        ServicoProduto servicoProdutoExistente = buscarPorId(id);

        servicoProdutoExistente.setNome(servicoProdutoAtualizado.getNome());
        servicoProdutoExistente.setProduto(servicoProdutoAtualizado.getProduto());
        servicoProdutoExistente.setDescricao(servicoProdutoAtualizado.getDescricao());
        servicoProdutoExistente.setDespesa(servicoProdutoAtualizado.getDespesa());
        servicoProdutoExistente.setValorVenda(servicoProdutoAtualizado.getValorVenda());

        return repository.save(servicoProdutoExistente);
    }
}
