package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.ComboDetalheResponse;
import lima.fernanda.esteticaFernandaLima.dto.ComboRequest;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.model.ComboServicoProduto;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComboService {

    private final ComboRepository repository;
    private final ServicoProdutoRepository servicoProdutoRepository;

    public ComboService(ComboRepository repository, ServicoProdutoRepository servicoProdutoRepository) {
        this.repository = repository;
        this.servicoProdutoRepository = servicoProdutoRepository;
    }

    // mantido como estava: outras partes do sistema (ex.: ordem de serviço) podem usar
    public List<Combo> buscarTodos() {
        return repository.findAll();
    }

    public Combo buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Combo não encontrado"));
    }

    /**
     * Compatibilidade com a API de serviço usada pelos testes e integrações
     * anteriores à adoção de ComboRequest.
     */
    @Transactional
    public Combo salvar(Combo combo) {
        return repository.save(combo);
    }

    /**
     * Mantido só por compatibilidade com testes antigos. Não apaga do banco:
     * aplica a exclusão lógica (inativar), igual ao restante do sistema.
     */
    @Deprecated
    @Transactional
    public void deletar(Integer id) {
        inativar(id);
    }

    @Transactional(readOnly = true)
    public List<ComboDetalheResponse> listarPorStatus(StatusCadastro status) {
        List<Combo> combos = switch (status) {
            case ATIVO -> repository.findByAtivo(true);
            case INATIVO -> repository.findByAtivo(false);
            case TODOS -> repository.findAll();
        };
        return combos.stream().map(ComboDetalheResponse::fromCombo).toList();
    }

    @Transactional(readOnly = true)
    public ComboDetalheResponse detalhar(Integer id) {
        return ComboDetalheResponse.fromCombo(buscarPorId(id));
    }

    @Transactional
    public ComboDetalheResponse criar(ComboRequest request) {
        Combo combo = new Combo();
        combo.setAtivo(true);
        combo.setDesconto(request.desconto() != null ? request.desconto() : 0f);
        preencher(combo, request);
        sincronizarItens(combo, request);
        return ComboDetalheResponse.fromCombo(repository.save(combo));
    }

    @Transactional
    public ComboDetalheResponse atualizar(Integer id, ComboRequest request) {
        Combo combo = buscarPorId(id);
        preencher(combo, request);
        // o PUT da versão web não envia desconto: só altera se vier
        if (request.desconto() != null) {
            combo.setDesconto(request.desconto());
        }
        sincronizarItens(combo, request);
        return ComboDetalheResponse.fromCombo(repository.save(combo));
    }

    @Transactional
    public Combo atualizar(Integer id, Combo comboAtualizado) {
        Combo combo = buscarPorId(id);
        combo.setNome(comboAtualizado.getNome());
        combo.setDescricao(comboAtualizado.getDescricao());
        combo.setValorFinal(comboAtualizado.getValorFinal());
        if (comboAtualizado.getDesconto() != null) {
            combo.setDesconto(comboAtualizado.getDesconto());
        }
        return repository.save(combo);
    }

    // exclusão lógica: o combo continua no banco, então vendas e dashboards não quebram
    @Transactional
    public ComboDetalheResponse inativar(Integer id) {
        return alterarStatus(id, false);
    }

    @Transactional
    public ComboDetalheResponse reativar(Integer id) {
        return alterarStatus(id, true);
    }

    private ComboDetalheResponse alterarStatus(Integer id, boolean ativo) {
        Combo combo = buscarPorId(id);
        combo.setAtivo(ativo);
        return ComboDetalheResponse.fromCombo(repository.save(combo));
    }

    private void preencher(Combo combo, ComboRequest request) {
        combo.setNome(request.nome());
        combo.setDescricao(request.descricao());
        combo.setValorFinal(request.valorFinal());
    }

    // Compara o que já está no combo com o que foi pedido: remove só o que saiu e adiciona só o que
    // entrou. Recriar tudo faria o Hibernate inserir antes de apagar e poderia bater em constraint única.
    private void sincronizarItens(Combo combo, ComboRequest request) {
        List<Integer> idsServicos = request.idsServicos();
        if (idsServicos == null) {
            return; // versão web: não mexe nos itens
        }
        if (idsServicos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O combo precisa ter pelo menos 1 serviço");
        }
        List<Integer> idsProdutos = request.idsProdutos() != null ? request.idsProdutos() : List.of();

        Set<Integer> desejados = new HashSet<>(idsServicos);
        desejados.addAll(idsProdutos);

        combo.getItensCombo().removeIf(
                item -> !desejados.contains(item.getServicoProduto().getIdProdutoServico()));

        Set<Integer> existentes = combo.getItensCombo().stream()
                .map(item -> item.getServicoProduto().getIdProdutoServico())
                .collect(Collectors.toSet());

        adicionarNovos(combo, idsServicos, false, existentes);
        adicionarNovos(combo, idsProdutos, true, existentes);
    }

    private void adicionarNovos(Combo combo, List<Integer> ids, boolean produto, Set<Integer> existentes) {
        for (Integer id : new LinkedHashSet<>(ids)) {
            if (existentes.contains(id)) {
                continue;
            }

            ServicoProduto servicoProduto = servicoProdutoRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item " + id + " não encontrado"));

            if (Boolean.TRUE.equals(servicoProduto.getProduto()) != produto) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "\"" + servicoProduto.getNome() + "\" não é " + (produto ? "um produto" : "um serviço"));
            }
            if (!Boolean.TRUE.equals(servicoProduto.getAtivo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "\"" + servicoProduto.getNome() + "\" está inativo");
            }

            ComboServicoProduto item = new ComboServicoProduto();
            item.setCombo(combo);
            item.setServicoProduto(servicoProduto);
            combo.getItensCombo().add(item);
        }
    }
}