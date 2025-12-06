package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import lima.fernanda.esteticaFernandaLima.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashSimplesService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepositoryrepository;

    @Autowired
    CustoExtraRepository extraRepository;

    @Autowired
    private CustoFixoService custoFixoService;

    public Float pegarRendaBrutaDoMesAtual() {
        List<Float> valoresFinais = ordemServicoRepositoryrepository.findValoresFinaisByMesAtual(LocalDate.now());
        Float rendaBruta = 0.00F;
        for (Float i : valoresFinais) {
            rendaBruta += i;
        }
        return rendaBruta;
    }

    public Float pegarRendaLiquidaDoMesAtual() {
        Float rendaBruta = pegarRendaBrutaDoMesAtual();

        List<Float> custosExtras = extraRepository.findValoresByMesAtual(LocalDate.now());
        Float totalCustosExtras = 0.00F;
        for (Float i : custosExtras) {
            totalCustosExtras += i;
        }

        List<Float> custosFixos = custoFixoService.listarTodos().stream()
                .map(custoFixo -> custoFixo.getValorMensal())
                .toList();

        Float totalCustosFixos = 0.00F;
        for (Float i : custosFixos) {
            totalCustosFixos += i;
        }

        return rendaBruta - totalCustosFixos - totalCustosExtras;
    }

    // {1=24.0, 2=34.0, 3=45.0, ...}

    public Map<Integer, Float> pegarRendaBrutaDeTodosOsMesesDoAnoAtual() {
        Map<Integer, Float> rendaBrutaPorMes = new HashMap<>();
        int anoAtual = LocalDate.now().getYear();

        for (int mes = 1; mes <= 12; mes++) {
            Float rendaBruta = ordemServicoRepositoryrepository.findValoresFinaisByAnoEMes(anoAtual, mes)
                    .stream()
                    .reduce(0.00F, Float::sum); // Soma apenas os valores do mês atual
            rendaBrutaPorMes.put(mes, rendaBruta);
        }

        return rendaBrutaPorMes;
    }

    public Map<Integer, Float> pegarRendaLiquidaDeTodosOsMesesDoAnoAtual() {
        Map<Integer, Float> rendaLiquidaPorMes = new HashMap<>();
        int anoAtual = LocalDate.now().getYear();

        for (int mes = 1; mes <= 12; mes++) {
            Float rendaBruta = ordemServicoRepositoryrepository.findValoresFinaisByAnoEMes(anoAtual, mes)
                    .stream()
                    .reduce(0.00F, Float::sum);

            Float totalCustosExtras = extraRepository.findValoresByAnoEMes(anoAtual, mes)
                    .stream()
                    .reduce(0.00F, Float::sum);

            Float totalCustosFixos = custoFixoService.listarTodos().stream()
                    .map(custoFixo -> custoFixo.getValorMensal())
                    .reduce(0.00F, Float::sum);

            Float rendaLiquida = rendaBruta - totalCustosExtras - totalCustosFixos;
            rendaLiquidaPorMes.put(mes, rendaLiquida);
        }

        return rendaLiquidaPorMes; // deve retornar algo como {1=15.0, 2=20.0, 3=25.0, ...
    }

    public Long pegarTotalDeOrdensDeServicoDoMesAtual() {
        return ordemServicoRepositoryrepository.countOrdensServicoByMesAtual(LocalDate.now());
    }
}
