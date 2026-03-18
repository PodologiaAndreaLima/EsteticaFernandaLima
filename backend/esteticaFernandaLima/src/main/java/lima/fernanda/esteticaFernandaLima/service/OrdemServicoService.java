package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoRequest;
import lima.fernanda.esteticaFernandaLima.model.*;
import lima.fernanda.esteticaFernandaLima.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoProdutoRepository servicoProdutoRepository;
    private final ComboRepository comboRepository;

    public OrdemServicoService(OrdemServicoRepository repository,
                               ClienteRepository clienteRepository,
                               UsuarioRepository usuarioRepository,
                               ServicoProdutoRepository servicoProdutoRepository,
                               ComboRepository comboRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoProdutoRepository = servicoProdutoRepository;
        this.comboRepository = comboRepository;
    }

    public List<OrdemServico> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public OrdemServico salvar(OrdemServicoRequest req) {
        OrdemServico ordem = new OrdemServico();

        if (req.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(req.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            ordem.setCliente(cliente);
        } else {
            throw new RuntimeException("Cliente é obrigatório");
        }

        if (req.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(req.getUsuarioId().longValue())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            ordem.setUsuario(usuario);
        } else {
            throw new RuntimeException("Usuário (funcionário) é obrigatório");
        }

        ordem.setValorFinal(req.getValorFinal());
        ordem.setObservacao(req.getObservacao());
        ordem.setDtHora(LocalDate.now());

        if (req.getItens() != null) {
            for (OrdemServicoRequest.ItemRequest itReq : req.getItens()) {
                VendaProdutoServico item = new VendaProdutoServico();
                item.setDesconto(itReq.getDesconto() != null ? itReq.getDesconto() : 0.0f);
                item.setQuantidade(itReq.getQuantidade() != null ? itReq.getQuantidade() : 1);

                if (itReq.getComboId() != null) {
                    Combo combo = comboRepository.findById(itReq.getComboId())
                            .orElseThrow(() -> new RuntimeException("Combo não encontrado: " + itReq.getComboId()));
                    item.setCombo(combo);
                } else if (itReq.getServicoProdutoId() != null) {
                    ServicoProduto sp = servicoProdutoRepository.findById(itReq.getServicoProdutoId())
                            .orElseThrow(() -> new RuntimeException("ServicoProduto não encontrado: " + itReq.getServicoProdutoId()));
                    item.setServicoProduto(sp);
                } else if (itReq.getProdutoId() != null) {
                    // produtoId também é um ServicoProduto (com isProduto = true)
                    ServicoProduto produto = servicoProdutoRepository.findById(itReq.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itReq.getProdutoId()));
                    item.setServicoProduto(produto);
                } else {
                    throw new RuntimeException("Item deve ter servicoProdutoId, comboId ou produtoId");
                }

                ordem.addItem(item);
            }
        }

        return repository.save(ordem);
    }

    @Transactional
    public OrdemServico atualizar(Integer id, OrdemServicoRequest req) {
        OrdemServico existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada"));

        if (req.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(req.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            existing.setCliente(cliente);
        }

        if (req.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(req.getUsuarioId().longValue())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            existing.setUsuario(usuario);
        }

        existing.setValorFinal(req.getValorFinal());
        existing.setObservacao(req.getObservacao());
        existing.getItens().clear();
        
        if (req.getItens() != null) {
            for (OrdemServicoRequest.ItemRequest itReq : req.getItens()) {
                VendaProdutoServico item = new VendaProdutoServico();
                item.setDesconto(itReq.getDesconto() != null ? itReq.getDesconto() : 0.0f);
                item.setQuantidade(itReq.getQuantidade() != null ? itReq.getQuantidade() : 1);
                
                if (itReq.getComboId() != null) {
                    Combo combo = comboRepository.findById(itReq.getComboId())
                            .orElseThrow(() -> new RuntimeException("Combo não encontrado: " + itReq.getComboId()));
                    item.setCombo(combo);
                } else if (itReq.getServicoProdutoId() != null) {
                    ServicoProduto sp = servicoProdutoRepository.findById(itReq.getServicoProdutoId())
                            .orElseThrow(() -> new RuntimeException("ServicoProduto não encontrado: " + itReq.getServicoProdutoId()));
                    item.setServicoProduto(sp);
                } else if (itReq.getProdutoId() != null) {
                    // produtoId também é um ServicoProduto (com isProduto = true)
                    ServicoProduto produto = servicoProdutoRepository.findById(itReq.getProdutoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itReq.getProdutoId()));
                    item.setServicoProduto(produto);
                }
                
                existing.addItem(item);
            }
        }

        return repository.save(existing);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Ordem de Serviço não encontrada");
        }
        repository.deleteById(id);
    }
}