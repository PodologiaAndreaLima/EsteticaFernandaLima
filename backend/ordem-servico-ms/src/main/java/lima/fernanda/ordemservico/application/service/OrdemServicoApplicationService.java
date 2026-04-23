package lima.fernanda.ordemservico.application.service;

import lima.fernanda.ordemservico.application.dto.OrdemServicoRequest;
import lima.fernanda.ordemservico.application.dto.OrdemServicoResponse;
import lima.fernanda.ordemservico.application.dto.PageResponse;
import lima.fernanda.ordemservico.application.mapper.OrdemServicoMapper;
import lima.fernanda.ordemservico.domain.exception.NotFoundException;
import lima.fernanda.ordemservico.domain.model.OrdemServico;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoEventPublisherPort;
import lima.fernanda.ordemservico.domain.repository.OrdemServicoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
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
        OrdemServico ordemServico = OrdemServicoMapper.toDomain(request);
        ordemServico.setDataCriacao(LocalDate.now());

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
        if (!repository.existsById(id)) {
            throw new NotFoundException("Ordem de servico nao encontrada");
        }

        OrdemServico ordemServico = OrdemServicoMapper.toDomain(request);
        ordemServico.setId(id);
        ordemServico.setDataCriacao(LocalDate.now());

        OrdemServico updated = repository.save(ordemServico);
        log.info("[OrdemServico] Atualizada com id={}", updated.getId());

        publisher.publicarOrdemAtualizada(updated);
        log.info("[OrdemServico] Evento UPDATE publicado para id={}", updated.getId());

        return OrdemServicoMapper.toResponse(updated);
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

