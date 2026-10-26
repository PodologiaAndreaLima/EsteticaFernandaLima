package lima.fernanda.ordemservico.application.service;

import lima.fernanda.ordemservico.application.dto.OrdemServicoRequest;
import lima.fernanda.ordemservico.application.dto.OrdemServicoResponse;
import lima.fernanda.ordemservico.application.dto.PageResponse;
import lima.fernanda.ordemservico.application.dto.StatusOrdemServicoRequest;
import lima.fernanda.ordemservico.application.mapper.OrdemServicoMapper;
import lima.fernanda.ordemservico.domain.exception.NotFoundException;
import lima.fernanda.ordemservico.domain.model.ItemOrdemServico;
import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.domain.model.StatusOrdemServico;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoEventPublisherPort;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrdemServicoApplicationService {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoApplicationService.class);
    private static final int FIXED_PAGE_SIZE = 10;

    private final OrdemServicoRepositoryPort repository;
    private final OrdemServicoEventPublisherPort publisher;

    public OrdemServicoApplicationService(
            OrdemServicoRepositoryPort repository,
            OrdemServicoEventPublisherPort publisher
    ) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public OrdemServicoResponse criar(OrdemServicoRequest request) {
        validar(request);
        OrdemServico ordemServico = OrdemServicoMapper.toDomain(request);
        ordemServico.setDtHora(request.dtHora().withNano(0));
        ordemServico.setDataCriacao(LocalDate.now());
        ordemServico.setStatus(StatusOrdemServico.PENDENTE);
        if (ordemServico.isRetorno()) {
            ordemServico.setDtHora(request.dtHora().toLocalDate().atStartOfDay());
        }
        aplicarValorFinal(ordemServico);

        OrdemServico saved = repository.save(ordemServico);
        log.info("[OrdemServico] Criada com id={}", saved.getId());

        publisher.publicarOrdemCriada(saved);
        log.info("[OrdemServico] Evento CREATE publicado para id={}", saved.getId());

        return OrdemServicoMapper.toResponse(saved);
    }

    public OrdemServicoResponse buscarPorId(Integer id) {
        OrdemServico ordemServico = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de servico nao encontrada"));
        return OrdemServicoMapper.toResponse(ordemServico);
    }

    public PageResponse<OrdemServicoResponse> listarPaginado(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("page deve ser >= 0");
        }

        Page<OrdemServico> resultado = repository.findAll(
                PageRequest.of(page, FIXED_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "dataCriacao"))
        );

        return new PageResponse<>(
                resultado.getContent().stream().map(OrdemServicoMapper::toResponse).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    public OrdemServicoResponse atualizar(Integer id, OrdemServicoRequest request) {
        validar(request);
        OrdemServico anterior = obter(id);
        if (request.retorno() != null && request.retorno() != anterior.isRetorno()) {
            throw new IllegalArgumentException("A identificação de retorno não pode ser alterada após o cadastro");
        }
        if (anterior.isRetorno() && !Objects.equals(request.dtHora(), anterior.getDtHora())) {
            throw new IllegalArgumentException("Data e hora do retorno só podem mudar na transição de status permitida");
        }
        OrdemServico ordemServico = OrdemServicoMapper.toDomain(request);
        ordemServico.setDtHora(request.dtHora().withNano(0));
        ordemServico.setId(id);
        ordemServico.setDataCriacao(anterior.getDataCriacao());
        ordemServico.setStatus(anterior.getStatus());
        ordemServico.setRetorno(anterior.isRetorno());
        preservarPrecosHistoricos(ordemServico, anterior);
        aplicarValorFinal(ordemServico);
        transicionar(ordemServico, request.status() == null ? anterior.getStatus() : request.status(), null);

        OrdemServico updated = repository.save(ordemServico);
        log.info("[OrdemServico] Atualizada com id={}", updated.getId());

        publisher.publicarOrdemAtualizada(updated);
        log.info("[OrdemServico] Evento UPDATE publicado para id={}", updated.getId());

        return OrdemServicoMapper.toResponse(updated);
    }

    public OrdemServicoResponse atualizarStatus(Integer id, StatusOrdemServicoRequest request) {
        OrdemServico ordem = obter(id);
        transicionar(ordem, request.status(), request.dtHora());
        OrdemServico saved = repository.save(ordem);
        publisher.publicarOrdemAtualizada(saved);
        return OrdemServicoMapper.toResponse(saved);
    }

    private OrdemServico obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Ordem de servico nao encontrada"));
    }

    private void transicionar(OrdemServico ordem, StatusOrdemServico destino, LocalDateTime dtHora) {
        if (destino == null || !ordem.getStatus().podeTransicionarPara(destino)) {
            throw new IllegalArgumentException("Transição de status não permitida");
        }
        if (dtHora != null && !dtHora.equals(ordem.getDtHora())) {
            boolean permitido = ordem.isRetorno() && ordem.getDtHora().toLocalTime().equals(LocalTime.MIDNIGHT)
                    && ordem.getStatus() != destino
                    && (destino == StatusOrdemServico.EM_ANDAMENTO || destino == StatusOrdemServico.CANCELADA);
            if (!permitido || !dtHora.toLocalDate().equals(ordem.getDtHora().toLocalDate())) {
                throw new IllegalArgumentException("Só o horário inicial do retorno pode mudar ao iniciar ou cancelar o atendimento");
            }
            ordem.setDtHora(dtHora.withNano(0));
        }
        ordem.setStatus(destino);
    }

    private void validar(OrdemServicoRequest request) {
        if (request.dtHora() == null || request.dtHora().getYear() < 1000 || request.dtHora().getYear() > 9999) {
            throw new IllegalArgumentException("Informe data e horário válidos para o atendimento");
        }
        if (request.valorFinal() == null || !Float.isFinite(request.valorFinal()) || request.valorFinal() < 0) {
            throw new IllegalArgumentException("Valor final inválido");
        }
        if (request.dataRetorno() != null && !request.dataRetorno().isAfter(request.dtHora().toLocalDate())) {
            throw new IllegalArgumentException("A data do retorno deve ser posterior à data do atendimento");
        }
    }

    /**
     * Itens que já estavam na OS mantêm o preço gravado no cadastro: a OS é o histórico do
     * atendimento e não acompanha mudanças de preço do catálogo.
     */
    private void preservarPrecosHistoricos(OrdemServico ordem, OrdemServico anterior) {
        for (ItemOrdemServico item : ordem.getItens()) {
            anterior.getItens().stream()
                    .filter(antigo -> antigo.getValorUnitario() != null && mesmaReferencia(item, antigo))
                    .findFirst()
                    .ifPresent(antigo -> item.setValorUnitario(antigo.getValorUnitario()));
        }
    }

    private static boolean mesmaReferencia(ItemOrdemServico a, ItemOrdemServico b) {
        return Objects.equals(a.getServicoProdutoId(), b.getServicoProdutoId())
                && Objects.equals(a.getProdutoId(), b.getProdutoId())
                && Objects.equals(a.getComboId(), b.getComboId());
    }

    /**
     * Valor final = soma de (preço unitário × quantidade − desconto do item), arredondada por item.
     * Só recalcula quando todos os itens trazem o preço; clientes antigos que ainda não enviam
     * valorUnitario continuam usando o valor informado.
     */
    private void aplicarValorFinal(OrdemServico ordem) {
        List<ItemOrdemServico> itens = ordem.getItens();
        if (itens.isEmpty() || itens.stream().anyMatch(item -> item.getValorUnitario() == null)) return;
        BigDecimal total = BigDecimal.ZERO;
        for (ItemOrdemServico item : itens) {
            BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade() == null ? 1 : item.getQuantidade());
            BigDecimal desconto = BigDecimal.valueOf(item.getDesconto() == null ? 0 : item.getDesconto());
            BigDecimal fator = BigDecimal.ONE.subtract(desconto.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            total = total.add(new BigDecimal(Float.toString(item.getValorUnitario()))
                    .multiply(quantidade).multiply(fator).setScale(2, RoundingMode.HALF_UP));
        }
        ordem.setValorFinal(total.floatValue());
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ordem de servico nao encontrada");
        }
        repository.deleteById(id);
        log.info("[OrdemServico] Deletada com id={}", id);

        publisher.publicarOrdemDeletada(id);
        log.info("[OrdemServico] Evento DELETE publicado para id={}", id);
    }
}

