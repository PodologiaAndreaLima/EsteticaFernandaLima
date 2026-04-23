package lima.fernanda.ordemservico.application.service;

import lima.fernanda.ordemservico.domain.event.OrdemServicoEvent;
import lima.fernanda.ordemservico.domain.service.OrdemServicoProcessamentoDomainService;
import org.springframework.stereotype.Service;

@Service
public class OrdemServicoProcessamentoApplicationService {

    private final OrdemServicoProcessamentoDomainService domainService;

    public OrdemServicoProcessamentoApplicationService(OrdemServicoProcessamentoDomainService domainService) {
        this.domainService = domainService;
    }

    public void processar(OrdemServicoEvent event) {
        domainService.processar(event);
    }
}

