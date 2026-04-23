package lima.fernanda.ordemservico.application.service;

import lima.fernanda.ordemservico.domain.service.OrdemServicoProcessamentoDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardApplicationService {

    private final OrdemServicoProcessamentoDomainService domainService;

    public DashboardApplicationService(OrdemServicoProcessamentoDomainService domainService) {
        this.domainService = domainService;
    }

    public Float getReceitaBrutaMensal(int mes, int ano) {
        return domainService.calcularReceitaBrutaMensal(mes, ano);
    }

    public Float getReceitaLiquidaMensal(int mes, int ano, float custosFixos, float custosExtras) {
        return domainService.calcularReceitaLiquidaMensal(mes, ano, custosFixos, custosExtras);
    }

    public Float getMargemLucroBruto(int mes, int ano, float custosFixos, float custosExtras) {
        return domainService.calcularMargemLucroBruto(mes, ano, custosFixos, custosExtras);
    }

    public Long getNumeroOrdensMensal(int mes, int ano) {
        return domainService.calcularNumeroOrdensMensal(mes, ano);
    }

    public Float getTicketMedio(int mes, int ano) {
        return domainService.calcularTicketMedio(mes, ano);
    }

    public Float getCustoMedioPorServico(int mes, int ano, float custosFixos, float custosExtras) {
        return domainService.calcularCustoMedioPorServico(mes, ano, custosFixos, custosExtras);
    }

    public Map<String, Float> getVariacaoMensal(int mes, int ano,
                                                 float custosFixosAtual, float custosExtrasAtual,
                                                 float custosFixosAnterior, float custosExtrasAnterior) {
        return domainService.calcularVariacaoMensal(mes, ano,
                custosFixosAtual, custosExtrasAtual,
                custosFixosAnterior, custosExtrasAnterior);
    }

    public Map<String, List<Float>> getReceitaAoLongoDosMeses(int ano,
                                                               Map<Integer, Float> custosFixosPorMes,
                                                               Map<Integer, Float> custosExtrasPorMes) {
        return domainService.calcularReceitaAoLongoDosMeses(ano, custosFixosPorMes, custosExtrasPorMes);
    }

    public Map<String, Float> getProporcaoCustos(int mes, int ano, float custosFixos, float custosExtras) {
        return domainService.calcularProporcaoCustos(mes, ano, custosFixos, custosExtras);
    }

    public List<Map<String, Object>> getTopServicos(int mes, int ano, int limite) {
        return domainService.calcularTopServicos(mes, ano, limite);
    }

    public List<Map<String, Object>> getTopProdutos(int mes, int ano, int limite) {
        return domainService.calcularTopProdutos(mes, ano, limite);
    }

    public Map<String, Long> getAtendimentosPorDiaSemana(int mes, int ano) {
        return domainService.calcularAtendimentosPorDiaSemana(mes, ano);
    }

    public Map<String, Float> getProporcaoAtendimentos(int mes, int ano) {
        return domainService.calcularProporcaoAtendimentos(mes, ano);
    }

    public Map<Integer, Float> getReceitaPorUsuario(int mes, int ano) {
        return domainService.calcularReceitaPorUsuario(mes, ano);
    }

    public Map<Integer, Long> getOrdensPorUsuario(int mes, int ano) {
        return domainService.calcularOrdensPorUsuario(mes, ano);
    }

    public Float getComissaoProprietario(int mes, int ano, float custosFixos,
                                          float custosExtras, float percentual) {
        return domainService.calcularComissaoProprietario(mes, ano, custosFixos, custosExtras, percentual);
    }
}
