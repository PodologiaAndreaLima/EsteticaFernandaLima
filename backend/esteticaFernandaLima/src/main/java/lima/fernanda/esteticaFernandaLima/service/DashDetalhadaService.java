package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsItemResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsPageResponse;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.dto.OrdemServicoMsResponse;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.repository.ComboRepository;
import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashDetalhadaService {

    private static final int FUNCIONARIO_ID_FIXO = 2;

    private final OrdemServicoMsClient ordemServicoMsClient;
    private final CustoExtraRepository custoExtraRepository;
    private final CustoFixoService custoFixoService;
    private final ServicoProdutoRepository servicoProdutoRepository;
    private final ComboRepository comboRepository;
    private final FuncionarioRepository funcionarioRepository;

    public DashDetalhadaService(OrdemServicoMsClient ordemServicoMsClient,
                              CustoExtraRepository custoExtraRepository,
                              CustoFixoService custoFixoService,
                              ServicoProdutoRepository servicoProdutoRepository,
                              ComboRepository comboRepository,
                              FuncionarioRepository funcionarioRepository) {
        this.ordemServicoMsClient = ordemServicoMsClient;
        this.custoExtraRepository = custoExtraRepository;
        this.custoFixoService = custoFixoService;
        this.servicoProdutoRepository = servicoProdutoRepository;
        this.comboRepository = comboRepository;
        this.funcionarioRepository = funcionarioRepository;
    }


    // Responsável por somar todos os valores das ordens de serviço no mês, sem descontar custos.
    public Float getReceitaBrutaMensal(int mes, int ano) {
        return (float) listarOrdensPorMesAno(mes, ano).stream()
                .map(OrdemServicoMsResponse::valorFinal)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    public Float getReceitaLiquidaMensal(int mes, int ano) {
        Float rendaBruta = getReceitaBrutaMensal(mes, ano);
        Float custosFixos = getCustosFixosMensais(mes, ano);
        Float custosExtras = getCustosExtrasMensais(mes, ano);
        return rendaBruta - custosFixos - custosExtras;
    }

    // Retorna o total de custos fixos cadastrados para o mês.
    public Float getCustosFixosMensais(int mes, int ano) {
        // Custos fixos são mensais, então soma todos os custos fixos ativos
        return custoFixoService.listarTodos().stream()
                .map(CustoFixo::getValorMensal)
                .filter(Objects::nonNull)
                .reduce(0.0F, Float::sum);
    }


    // Soma todos os custos variáveis
    public Float getCustosExtrasMensais(int mes, int ano) {
        return custoExtraRepository.findValoresByAnoEMes(ano, mes).stream()
                .filter(Objects::nonNull)
                .reduce(0.0F, Float::sum);
    }

    public Float getMargemLucroBruto(int mes, int ano) {
        Float receitaBruta = getReceitaBrutaMensal(mes, ano);
        if (receitaBruta == 0.0F) return 0.0F;
        Float receitaLiquida = getReceitaLiquidaMensal(mes, ano);
        return (receitaLiquida / receitaBruta) * 100;
    }

    public Map<String, Float> getVariacaoMensal(int mesAtual, int anoAtual) {
        Map<String, Float> variacao = new HashMap<>();

        Float receitaAtual = getReceitaBrutaMensal(mesAtual, anoAtual);
        Float custosAtuais = getCustosFixosMensais(mesAtual, anoAtual) + getCustosExtrasMensais(mesAtual, anoAtual);

        int mesAnterior = mesAtual == 1 ? 12 : mesAtual - 1;
        int anoAnterior = mesAtual == 1 ? anoAtual - 1 : anoAtual;

        Float receitaAnterior = getReceitaBrutaMensal(mesAnterior, anoAnterior);
        Float custosAnteriores = getCustosFixosMensais(mesAnterior, anoAnterior) + getCustosExtrasMensais(mesAnterior, anoAnterior);

        // Variação de receita (%)
        if (receitaAnterior != 0.0F) {
            variacao.put("variacaoReceita", ((receitaAtual - receitaAnterior) / receitaAnterior) * 100);
        } else {
            variacao.put("variacaoReceita", receitaAtual > 0 ? 100.0F : 0.0F);
        }

        // Variação de custos (%)
        if (custosAnteriores != 0.0F) {
            variacao.put("variacaoCustos", ((custosAtuais - custosAnteriores) / custosAnteriores) * 100);
        } else {
            variacao.put("variacaoCustos", custosAtuais > 0 ? 100.0F : 0.0F);
        }

        return variacao;
    }

    public Map<String, List<Float>> getReceitaAoLongoDosMeses(int ano) {
        Map<String, List<Float>> receitas = new HashMap<>();
        List<Float> receitaBruta = new ArrayList<>();
        List<Float> receitaLiquida = new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {
            receitaBruta.add(getReceitaBrutaMensal(mes, ano));
            receitaLiquida.add(getReceitaLiquidaMensal(mes, ano));
        }

        receitas.put("receitaBruta", receitaBruta);
        receitas.put("receitaLiquida", receitaLiquida);
        return receitas;
    }

    // Retorna proporção percentual: Custos fixos, Custos extras, Lucro
    public Map<String, Float> getProporcaoCustos(int mes, int ano) {
        Map<String, Float> proporcao = new HashMap<>();
        Float receitaBruta = getReceitaBrutaMensal(mes, ano);
        if (receitaBruta == 0.0F) {
            proporcao.put("custosFixos", 0.0F);
            proporcao.put("custosExtras", 0.0F);
            proporcao.put("lucro", 0.0F);
            return proporcao;
        }

        Float custosFixos = getCustosFixosMensais(mes, ano);
        Float custosExtras = getCustosExtrasMensais(mes, ano);
        Float lucro = receitaBruta - custosFixos - custosExtras;

        proporcao.put("custosFixos", (custosFixos / receitaBruta) * 100);
        proporcao.put("custosExtras", (custosExtras / receitaBruta) * 100);
        proporcao.put("lucro", (lucro / receitaBruta) * 100);
        return proporcao;
    }

    // Conta quantas ordens de serviço foram realizadas no mês.
    public Long getNumeroOrdensMensal(int mes, int ano) {
        return (long) listarOrdensPorMesAno(mes, ano).size();
    }

    // Média de valor por cliente: (receita bruta / número de ordens)
    public Float getTicketMedio(int mes, int ano) {
        Long numeroOrdens = getNumeroOrdensMensal(mes, ano);
        if (numeroOrdens == 0) return 0.0F;
        Float receitaBruta = getReceitaBrutaMensal(mes, ano);
        return receitaBruta / numeroOrdens;
    }


    // Calcula o custo médio por atendimento.
    public Float getCustoMedioPorServico(int mes, int ano) {
        Long numeroOrdens = getNumeroOrdensMensal(mes, ano);
        if (numeroOrdens == 0) return 0.0F;
        Float custosTotais = getCustosFixosMensais(mes, ano) + getCustosExtrasMensais(mes, ano);
        return custosTotais / numeroOrdens;
    }

    // Retorna os serviços/combos mais realizados no mês, ordenados por quantidade.
    public List<ServicoQuantidadeDTO> getTopServicos(int mes, int ano, int limite) {
        Map<String, Long> quantidadePorNome = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            List<OrdemServicoMsItemResponse> itens = ordem.itens();
            if (itens == null) continue;

            for (OrdemServicoMsItemResponse item : itens) {
                int quantidade = item.quantidade() != null ? item.quantidade() : 1;

                if (item.comboId() != null) {
                    String nomeCombo = buscarNomeCombo(item.comboId());
                    quantidadePorNome.merge(nomeCombo, (long) quantidade, Long::sum);
                    continue;
                }

                Integer servicoId = item.servicoProdutoId() != null ? item.servicoProdutoId() : item.produtoId();
                if (servicoId == null) continue;

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
                .limit(limite)
                .map(entry -> new ServicoQuantidadeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // Retorna os produtos mais vendidos no mês.
    public List<ProdutoQuantidadeDTO> getTopProdutos(int mes, int ano, int limite) {
        Map<String, Long> quantidadePorNome = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            List<OrdemServicoMsItemResponse> itens = ordem.itens();
            if (itens == null) continue;

            for (OrdemServicoMsItemResponse item : itens) {
                Integer produtoId = item.produtoId() != null ? item.produtoId() : item.servicoProdutoId();
                if (produtoId == null) continue;

                Optional<ServicoProduto> produtoOpt = servicoProdutoRepository.findById(produtoId);
                if (produtoOpt.isEmpty() || !Boolean.TRUE.equals(produtoOpt.get().getProduto())) continue;

                int quantidade = item.quantidade() != null ? item.quantidade() : 1;
                String nomeProduto = produtoOpt.get().getNome() != null
                        ? produtoOpt.get().getNome()
                        : "Produto #" + produtoId;
                quantidadePorNome.merge(nomeProduto, (long) quantidade, Long::sum);
            }
        }

        return quantidadePorNome.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limite)
                .map(entry -> new ProdutoQuantidadeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // Retorna quantidade de atendimentos agrupados por dia da semana.
    public Map<String, Long> getAtendimentosPorDiaSemana(int mes, int ano) {
        Map<String, Long> atendimentosPorDia = new LinkedHashMap<>();
        // Inicializar com dias da semana
        atendimentosPorDia.put("Segunda", 0L);
        atendimentosPorDia.put("Terça", 0L);
        atendimentosPorDia.put("Quarta", 0L);
        atendimentosPorDia.put("Quinta", 0L);
        atendimentosPorDia.put("Sexta", 0L);
        atendimentosPorDia.put("Sábado", 0L);
        atendimentosPorDia.put("Domingo", 0L);

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            if (ordem.dataCriacao() == null) continue;
            String diaSemana = getDiaSemana(ordem.dataCriacao().getDayOfWeek().getValue());
            atendimentosPorDia.merge(diaSemana, 1L, Long::sum);
        }

        return atendimentosPorDia;
    }

    // Percentual de atendimentos por tipo: Serviços, Produtos, Combos
    public Map<String, Float> getProporcaoAtendimentos(int mes, int ano) {
        Map<String, Integer> contagemPorTipo = new HashMap<>();
        contagemPorTipo.put("Serviços", 0);
        contagemPorTipo.put("Produtos", 0);
        contagemPorTipo.put("Combos", 0);

        int totalItens = 0;

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            List<OrdemServicoMsItemResponse> itens = ordem.itens();
            if (itens == null) continue;

            for (OrdemServicoMsItemResponse item : itens) {
                totalItens += item.quantidade() != null ? item.quantidade() : 1;

                if (item.comboId() != null) {
                    contagemPorTipo.merge("Combos", item.quantidade() != null ? item.quantidade() : 1, Integer::sum);
                } else if (item.produtoId() != null) {
                    contagemPorTipo.merge("Produtos", item.quantidade() != null ? item.quantidade() : 1, Integer::sum);
                } else if (item.servicoProdutoId() != null) {
                    Optional<ServicoProduto> spOpt = servicoProdutoRepository.findById(item.servicoProdutoId());
                    if (spOpt.isPresent()) {
                        if (Boolean.TRUE.equals(spOpt.get().getProduto())) {
                            contagemPorTipo.merge("Produtos", item.quantidade() != null ? item.quantidade() : 1, Integer::sum);
                        } else {
                            contagemPorTipo.merge("Serviços", item.quantidade() != null ? item.quantidade() : 1, Integer::sum);
                        }
                    }
                }
            }
        }

        Map<String, Float> proporcao = new HashMap<>();
        if (totalItens == 0) {
            proporcao.put("Serviços", 0.0F);
            proporcao.put("Produtos", 0.0F);
            proporcao.put("Combos", 0.0F);
            return proporcao;
        }

        proporcao.put("Serviços", ((float) contagemPorTipo.get("Serviços") / totalItens) * 100);
        proporcao.put("Produtos", ((float) contagemPorTipo.get("Produtos") / totalItens) * 100);
        proporcao.put("Combos", ((float) contagemPorTipo.get("Combos") / totalItens) * 100);
        return proporcao;
    }

    // Retorna quanto cada funcionário gerou de receita no mês.
    public Map<String, Float> getReceitaPorFuncionario(int mes, int ano) {
        Map<Integer, Float> receitaPorUsuarioId = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            if (ordem.usuarioId() == null || ordem.valorFinal() == null) continue;
            receitaPorUsuarioId.merge(ordem.usuarioId(), ordem.valorFinal(), Float::sum);
        }

        Map<String, Float> receitaPorFuncionario = new HashMap<>();
        for (Map.Entry<Integer, Float> entry : receitaPorUsuarioId.entrySet()) {
            String nomeFuncionario = funcionarioRepository.findById(entry.getKey())
                    .map(Funcionario::getNome)
                    .orElse("Funcionário #" + entry.getKey());
            receitaPorFuncionario.put(nomeFuncionario, entry.getValue());
        }

        return receitaPorFuncionario;
    }

    // Calcula comissão baseada na receita líquida.
    public Float getComissaoProprietario(int mes, int ano, float percentual) {
        Float receitaLiquida = getReceitaLiquidaMensal(mes, ano);
        return receitaLiquida * (percentual / 100);
    }

    // Quantidade de atendimentos realizados por cada funcionário.
    public Map<String, Long> getOrdensPorFuncionario(int mes, int ano) {
        Map<Integer, Long> ordensPorUsuarioId = new HashMap<>();

        for (OrdemServicoMsResponse ordem : listarOrdensPorMesAno(mes, ano)) {
            if (ordem.usuarioId() == null) continue;
            ordensPorUsuarioId.merge(ordem.usuarioId(), 1L, Long::sum);
        }

        Map<String, Long> ordensPorFuncionario = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : ordensPorUsuarioId.entrySet()) {
            String nomeFuncionario = funcionarioRepository.findById(entry.getKey())
                    .map(Funcionario::getNome)
                    .orElse("Funcionário #" + entry.getKey());
            ordensPorFuncionario.put(nomeFuncionario, entry.getValue());
        }

        return ordensPorFuncionario;
    }

    // Distribuição de atendimentos por dia da semana considerando todos funcionários.
    public Map<String, Long> getAtendimentosFuncionarioPorDiaSemana(int mes, int ano) {
        return getAtendimentosPorDiaSemana(mes, ano); // Mesmo que o 14, pois já agrupa todos
    }

    // Métodos auxiliares
    private List<OrdemServicoMsResponse> listarOrdensPorMesAno(int mes, int ano) {
        return listarTodasOrdensDoMs().stream()
                .filter(os -> os.dataCriacao() != null)
                .filter(os -> os.dataCriacao().getYear() == ano && os.dataCriacao().getMonthValue() == mes)
                .toList();
    }

    private String getDiaSemana(int dayOfWeek) { // 1=Monday, 7=Sunday
        return switch (dayOfWeek) {
            case 1 -> "Segunda";
            case 2 -> "Terça";
            case 3 -> "Quarta";
            case 4 -> "Quinta";
            case 5 -> "Sexta";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "Desconhecido";
        };
    }

    // Métodos existentes mantidos para compatibilidade
    public Float pegarRendaBrutaDoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return getReceitaBrutaMensal(hoje.getMonthValue(), hoje.getYear());
    }

    public Float pegarRendaLiquidaDoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return getReceitaLiquidaMensal(hoje.getMonthValue(), hoje.getYear());
    }

    public Map<Integer, Float> pegarRendaBrutaDeTodosOsMesesDoAnoAtual() {
        int ano = LocalDate.now().getYear();
        Map<Integer, Float> map = new HashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            map.put(mes, getReceitaBrutaMensal(mes, ano));
        }
        return map;
    }

    public Map<Integer, Float> pegarRendaLiquidaDeTodosOsMesesDoAnoAtual() {
        int ano = LocalDate.now().getYear();
        Map<Integer, Float> map = new HashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            map.put(mes, getReceitaLiquidaMensal(mes, ano));
        }
        return map;
    }

    public Long pegarTotalDeOrdensDeServicoDoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return getNumeroOrdensMensal(hoje.getMonthValue(), hoje.getYear());
    }

    public List<ServicoQuantidadeDTO> pegarServicosOuComboMaisVendidosDoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return getTopServicos(hoje.getMonthValue(), hoje.getYear(), 5);
    }

    public List<ProdutoQuantidadeDTO> pegarProdutosMaisVendidosDoMesAtual() {
        LocalDate hoje = LocalDate.now();
        return getTopProdutos(hoje.getMonthValue(), hoje.getYear(), 5);
    }

    public Float pegarReceitaTotalFuncionarioMesAtual() {
        LocalDate hoje = LocalDate.now();
        Map<String, Float> receitaPorFunc = getReceitaPorFuncionario(hoje.getMonthValue(), hoje.getYear());
        // Retorna a receita do funcionário fixo, ou 0 se não encontrado
        return receitaPorFunc.getOrDefault("Funcionário #" + FUNCIONARIO_ID_FIXO, 0.0F);
    }

    public Long pegarQuantidadeOrdensFuncionarioMesAtual() {
        LocalDate hoje = LocalDate.now();
        Map<String, Long> ordensPorFunc = getOrdensPorFuncionario(hoje.getMonthValue(), hoje.getYear());
        return ordensPorFunc.getOrDefault("Funcionário #" + FUNCIONARIO_ID_FIXO, 0L);
    }

    private String buscarNomeCombo(Integer comboId) {
        return comboRepository.findById(comboId)
                .map(Combo::getNome)
                .filter(nome -> !nome.isBlank())
                .orElse("Combo #" + comboId);
    }

    private List<OrdemServicoMsResponse> listarOrdensAnoAtual() {
        int anoAtual = LocalDate.now().getYear();
        return listarOrdensPorMesAno(0, anoAtual); // Ajustar se necessário
    }

    private List<OrdemServicoMsResponse> listarOrdensMesAtual() {
        LocalDate hoje = LocalDate.now();
        return listarOrdensPorMesAno(hoje.getMonthValue(), hoje.getYear());
    }

    private List<OrdemServicoMsResponse> listarTodasOrdensDoMs() {
        List<OrdemServicoMsResponse> resultado = new ArrayList<>();
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
