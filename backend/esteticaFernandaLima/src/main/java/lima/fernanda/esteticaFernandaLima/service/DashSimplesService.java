package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsItemResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsPageResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashSimplesService {

    private static final int PROPRIETARIA_ID = 1;

    private final OrdemServicoMsClient ordemServicoMsClient;
    private final CustoExtraRepository extraRepository;
    private final CustoFixoService custoFixoService;
    private final ServicoProdutoRepository servicoProdutoRepository;
    private final ComboRepository comboRepository;

    public DashSimplesService(OrdemServicoMsClient ordemServicoMsClient,
                              CustoExtraRepository extraRepository,
                              CustoFixoService custoFixoService,
                              ServicoProdutoRepository servicoProdutoRepository,
                              ComboRepository comboRepository) {
        this.ordemServicoMsClient = ordemServicoMsClient;
        this.extraRepository = extraRepository;
        this.custoFixoService = custoFixoService;
        this.servicoProdutoRepository = servicoProdutoRepository;
        this.comboRepository = comboRepository;
    }

    public Float pegarRendaBrutaDoMesAtual() {
        return (float) listarOrdensMesAtual().stream()
                .map(OrdemServicoMsResponse::valorFinal)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    public Float pegarRendaLiquidaDoMesAtual() {
        Float rendaBruta = pegarRendaBrutaDoMesAtual();

        Float totalCustosExtras = extraRepository.findValoresByMesAtual(LocalDate.now())
                .stream()
                .reduce(0.00F, Float::sum);

        Float totalCustosFixos = custoFixoService.listarTodos().stream()
                .map(custoFixo -> custoFixo.getValorMensal())
                .reduce(0.00F, Float::sum);

        return rendaBruta - totalCustosFixos - totalCustosExtras;
    }

    public Map<Integer, Float> pegarRendaBrutaDeTodosOsMesesDoAnoAtual() {
        Map<Integer, Float> rendaBrutaPorMes = new HashMap<>();
        int anoAtual = LocalDate.now().getYear();

        List<OrdemServicoMsResponse> ordensAnoAtual = listarOrdensAnoAtual();

        for (int mes = 1; mes <= 12; mes++) {
            int mesAtualLoop = mes;
            Float rendaBruta = (float) ordensAnoAtual.stream()
                    .filter(os -> os.dataCriacao() != null)
                    .filter(os -> os.dataCriacao().getMonthValue() == mesAtualLoop)
                    .map(OrdemServicoMsResponse::valorFinal)
                    .filter(java.util.Objects::nonNull)
                    .mapToDouble(Float::doubleValue)
                    .sum();
            rendaBrutaPorMes.put(mes, rendaBruta);
        }

        return rendaBrutaPorMes;
    }

    public Map<Integer, Float> pegarRendaLiquidaDeTodosOsMesesDoAnoAtual() {
        Map<Integer, Float> rendaLiquidaPorMes = new HashMap<>();
        int anoAtual = LocalDate.now().getYear();

        List<OrdemServicoMsResponse> ordensAnoAtual = listarOrdensAnoAtual();

        for (int mes = 1; mes <= 12; mes++) {
            int mesAtualLoop = mes;
            Float rendaBruta = (float) ordensAnoAtual.stream()
                    .filter(os -> os.dataCriacao() != null)
                    .filter(os -> os.dataCriacao().getMonthValue() == mesAtualLoop)
                    .map(OrdemServicoMsResponse::valorFinal)
                    .filter(java.util.Objects::nonNull)
                    .mapToDouble(Float::doubleValue)
                    .sum();

            Float totalCustosExtras = extraRepository.findValoresByAnoEMes(anoAtual, mes)
                    .stream()
                    .reduce(0.00F, Float::sum);

            Float totalCustosFixos = custoFixoService.listarTodos().stream()
                    .map(custoFixo -> custoFixo.getValorMensal())
                    .reduce(0.00F, Float::sum);

            rendaLiquidaPorMes.put(mes, rendaBruta - totalCustosExtras - totalCustosFixos);
        }

        return rendaLiquidaPorMes;
    }

    public Long pegarTotalDeOrdensDeServicoDoMesAtual() {
        return (long) listarOrdensMesAtual().size();
    }

    public List<ServicoQuantidadeDTO> pegarServicosOuComboMaisVendidosDoMesAtual() {
        Map<String, Long> quantidadePorNome = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensMesAtual()) {
            List<OrdemServicoMsItemResponse> itens = ordem.itens();
            if (itens == null) {
                continue;
            }

            for (OrdemServicoMsItemResponse item : itens) {
                int quantidade = item.quantidade() != null ? item.quantidade() : 1;

                if (item.comboId() != null) {
                    String nomeCombo = buscarNomeCombo(item.comboId());
                    quantidadePorNome.merge(nomeCombo, (long) quantidade, Long::sum);
                    continue;
                }

                Integer servicoId = item.servicoProdutoId() != null ? item.servicoProdutoId() : item.produtoId();
                if (servicoId == null) {
                    continue;
                }

                Optional<ServicoProduto> servicoProdutoOpt = servicoProdutoRepository.findById(servicoId);
                if (servicoProdutoOpt.isPresent() && Boolean.FALSE.equals(servicoProdutoOpt.get().getProduto())) {
                    String nomeServico = servicoProdutoOpt.get().getNome() != null
                            ? servicoProdutoOpt.get().getNome()
                            : "Servico #" + servicoId;
                    quantidadePorNome.merge(nomeServico, (long) quantidade, Long::sum);
                }
            }
        }

        return quantidadePorNome.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> new ServicoQuantidadeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ProdutoQuantidadeDTO> pegarProdutosMaisVendidosDoMesAtual() {
        Map<String, Long> quantidadePorNome = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensMesAtual()) {
            List<OrdemServicoMsItemResponse> itens = ordem.itens();
            if (itens == null) {
                continue;
            }

            for (OrdemServicoMsItemResponse item : itens) {
                Integer produtoId = item.produtoId() != null ? item.produtoId() : item.servicoProdutoId();
                if (produtoId == null) {
                    continue;
                }

                Optional<ServicoProduto> produtoOpt = servicoProdutoRepository.findById(produtoId);
                if (produtoOpt.isEmpty() || !Boolean.TRUE.equals(produtoOpt.get().getProduto())) {
                    continue;
                }

                int quantidade = item.quantidade() != null ? item.quantidade() : 1;
                String nomeProduto = produtoOpt.get().getNome() != null
                        ? produtoOpt.get().getNome()
                        : "Produto #" + produtoId;
                quantidadePorNome.merge(nomeProduto, (long) quantidade, Long::sum);
            }
        }

        return quantidadePorNome.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> new ProdutoQuantidadeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Float pegarReceitaTotalFuncionarioMesAtual(Integer funcionarioId) {
        return (float) listarOrdensMesAtual().stream()
                .filter(ordem -> ordem.usuarioId() != null)
                .filter(ordem -> pertenceAoFiltroFuncionario(ordem.usuarioId(), funcionarioId))
                .map(OrdemServicoMsResponse::valorFinal)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    public Float pegarReceitaComissionavelProprietariaMesAtual(Integer proprietarioId) {
        return (float) listarOrdensMesAtual().stream()
                .filter(ordem -> ordem.usuarioId() != null)
                .filter(ordem -> proprietarioId == null || !ordem.usuarioId().equals(proprietarioId))
                .map(OrdemServicoMsResponse::valorFinal)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    public Long pegarQuantidadeOrdensFuncionarioMesAtual(Integer funcionarioId) {
        return listarOrdensMesAtual().stream()
                .filter(ordem -> ordem.usuarioId() != null)
                .filter(ordem -> pertenceAoFiltroFuncionario(ordem.usuarioId(), funcionarioId))
                .count();
    }

    private boolean pertenceAoFiltroFuncionario(Integer usuarioId, Integer funcionarioId) {
        if (funcionarioId == null) {
            return true;
        }

        if (funcionarioId == PROPRIETARIA_ID) {
            return !usuarioId.equals(PROPRIETARIA_ID);
        }

        return usuarioId.equals(funcionarioId);
    }

    private String buscarNomeCombo(Integer comboId) {
        return comboRepository.findById(comboId)
                .map(Combo::getNome)
                .filter(nome -> !nome.isBlank())
                .orElse("Combo #" + comboId);
    }

    private List<OrdemServicoMsResponse> listarOrdensAnoAtual() {
        int anoAtual = LocalDate.now().getYear();
        return listarTodasOrdensDoMs().stream()
                .filter(os -> os.dataCriacao() != null)
                .filter(os -> os.dataCriacao().getYear() == anoAtual)
                .toList();
    }

    private List<OrdemServicoMsResponse> listarOrdensMesAtual() {
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();

        return listarTodasOrdensDoMs().stream()
                .filter(os -> os.dataCriacao() != null)
                .filter(os -> os.dataCriacao().getYear() == ano && os.dataCriacao().getMonthValue() == mes)
                .toList();
    }

    private List<OrdemServicoMsResponse> listarTodasOrdensDoMs() {
        List<OrdemServicoMsResponse> resultado = new java.util.ArrayList<>();
        int page = 0;

        while (true) {
            OrdemServicoMsPageResponse response = ordemServicoMsClient.listarPaginado(page);
            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                break;
            }

            resultado.addAll(response.getContent());

            if (page >= response.getTotalPages() - 1) {
                break;
            }
            page++;
        }

        return resultado;
    }
}
