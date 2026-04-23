package lima.fernanda.ordemservico.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.ordemservico.domain.event.OrdemServicoEvent;
import lima.fernanda.ordemservico.domain.model.DashboardOrdemServico;
import lima.fernanda.ordemservico.domain.repository.DashboardOrdemServicoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


public class OrdemServicoProcessamentoDomainService {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoProcessamentoDomainService.class);

    private final DashboardOrdemServicoRepositoryPort dashboardRepository;
    private final ObjectMapper objectMapper;

    public OrdemServicoProcessamentoDomainService(DashboardOrdemServicoRepositoryPort dashboardRepository,
                                                   ObjectMapper objectMapper) {
        this.dashboardRepository = dashboardRepository;
        this.objectMapper = objectMapper;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Processamento de eventos (entrada via fila)
    // ══════════════════════════════════════════════════════════════════════════

    public void processar(OrdemServicoEvent event) {
        log.info("[DomainService] Processando evento {} para ordemServicoId={}",
                event.getTipoOperacao(), event.getOrdemServicoId());

        switch (event.getTipoOperacao()) {
            case CREATE -> processarCriacao(event);
            case UPDATE -> processarAtualizacao(event);
            case DELETE -> processarDelecao(event);
            default -> log.warn("[DomainService] TipoOperacao desconhecido: {}", event.getTipoOperacao());
        }
    }

    private void processarCriacao(OrdemServicoEvent event) {
        // Idempotência: se já existe, atualiza (re-entrega da mensagem)
        DashboardOrdemServico dashboard = dashboardRepository
                .findByOrdemServicoId(event.getOrdemServicoId())
                .orElse(new DashboardOrdemServico());

        preencherDashboard(dashboard, event);
        dashboardRepository.save(dashboard);
        log.info("[DomainService] Dashboard CRIADO/UPSERT para ordemServicoId={}", event.getOrdemServicoId());
    }

    private void processarAtualizacao(OrdemServicoEvent event) {
        DashboardOrdemServico dashboard = dashboardRepository
                .findByOrdemServicoId(event.getOrdemServicoId())
                .orElse(new DashboardOrdemServico());

        preencherDashboard(dashboard, event);
        dashboardRepository.save(dashboard);
        log.info("[DomainService] Dashboard ATUALIZADO para ordemServicoId={}", event.getOrdemServicoId());
    }

    private void processarDelecao(OrdemServicoEvent event) {
        dashboardRepository.deleteByOrdemServicoId(event.getOrdemServicoId());
        log.info("[DomainService] Dashboard REMOVIDO para ordemServicoId={}", event.getOrdemServicoId());
    }

    private void preencherDashboard(DashboardOrdemServico dashboard, OrdemServicoEvent event) {
        dashboard.setOrdemServicoId(event.getOrdemServicoId());
        dashboard.setClienteId(event.getClienteId());
        dashboard.setUsuarioId(event.getUsuarioId());
        dashboard.setValorFinal(arredondar(event.getValorFinal()));
        dashboard.setDataCriacao(event.getDataCriacao());
        dashboard.setStatus("ATIVO");

        if (event.getDataCriacao() != null) {
            dashboard.setMes(event.getDataCriacao().getMonthValue());
            dashboard.setAno(event.getDataCriacao().getYear());
            dashboard.setDiaSemana(resolverDiaSemana(event.getDataCriacao().getDayOfWeek().getValue()));
        }

        dashboard.setItensJson(serializarItens(event.getItens()));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Cálculos de dashboard — equivalentes ao DashDetalhadaService do back-scr
    // ══════════════════════════════════════════════════════════════════════════

    /** Soma o valorFinal de todas as ordens do mês/ano. */
    public Float calcularReceitaBrutaMensal(int mes, int ano) {
        return (float) dashboardRepository.findByMesAndAno(mes, ano).stream()
                .map(DashboardOrdemServico::getValorFinal)
                .filter(Objects::nonNull)
                .mapToDouble(Float::doubleValue)
                .sum();
    }

    /** Receita líquida = receita bruta − custos fixos − custos extras.*/
    public Float calcularReceitaLiquidaMensal(int mes, int ano, float custosFixos, float custosExtras) {
        return calcularReceitaBrutaMensal(mes, ano) - custosFixos - custosExtras;
    }

    public Float calcularMargemLucroBruto(int mes, int ano, float custosFixos, float custosExtras) {
        Float receitaBruta = calcularReceitaBrutaMensal(mes, ano);
        if (receitaBruta == 0f) return 0f;
        Float receitaLiquida = calcularReceitaLiquidaMensal(mes, ano, custosFixos, custosExtras);
        return (receitaLiquida / receitaBruta) * 100;
    }

    /** Contagem de ordens no mês. */
    public Long calcularNumeroOrdensMensal(int mes, int ano) {
        return (long) dashboardRepository.findByMesAndAno(mes, ano).size();
    }

    /** Ticket médio = receita bruta / número de ordens. */
    public Float calcularTicketMedio(int mes, int ano) {
        Long total = calcularNumeroOrdensMensal(mes, ano);
        if (total == 0) return 0f;
        return calcularReceitaBrutaMensal(mes, ano) / total;
    }

    /** Custo médio por atendimento = custos totais / número de ordens. */
    public Float calcularCustoMedioPorServico(int mes, int ano, float custosFixos, float custosExtras) {
        Long total = calcularNumeroOrdensMensal(mes, ano);
        if (total == 0) return 0f;
        return (custosFixos + custosExtras) / total;
    }

    /**
     * Variação percentual de receita e custos em relação ao mês anterior.
     */
    public Map<String, Float> calcularVariacaoMensal(int mes, int ano,
                                                      float custosFixosAtual, float custosExtrasAtual,
                                                      float custosFixosAnterior, float custosExtrasAnterior) {
        Map<String, Float> variacao = new HashMap<>();

        int mesAnterior = mes == 1 ? 12 : mes - 1;
        int anoAnterior = mes == 1 ? ano - 1 : ano;

        Float receitaAtual = calcularReceitaBrutaMensal(mes, ano);
        Float custosAtuais = custosFixosAtual + custosExtrasAtual;

        Float receitaAnterior = calcularReceitaBrutaMensal(mesAnterior, anoAnterior);
        Float custosAnteriores = custosFixosAnterior + custosExtrasAnterior;

        variacao.put("variacaoReceita", calcularVariacao(receitaAnterior, receitaAtual));
        variacao.put("variacaoCustos", calcularVariacao(custosAnteriores, custosAtuais));
        return variacao;
    }

    /**
     * Receita bruta e líquida mês a mês ao longo de um ano.
     */
    public Map<String, List<Float>> calcularReceitaAoLongoDosMeses(int ano,
                                                                     Map<Integer, Float> custosFixosPorMes,
                                                                     Map<Integer, Float> custosExtrasPorMes) {
        List<Float> bruta = new ArrayList<>();
        List<Float> liquida = new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {
            Float rb = calcularReceitaBrutaMensal(mes, ano);
            float cf = custosFixosPorMes.getOrDefault(mes, 0f);
            float ce = custosExtrasPorMes.getOrDefault(mes, 0f);
            bruta.add(rb);
            liquida.add(rb - cf - ce);
        }

        Map<String, List<Float>> resultado = new HashMap<>();
        resultado.put("receitaBruta", bruta);
        resultado.put("receitaLiquida", liquida);
        return resultado;
    }


    public Map<String, Float> calcularProporcaoCustos(int mes, int ano,
                                                       float custosFixos, float custosExtras) {
        Map<String, Float> proporcao = new HashMap<>();
        Float receitaBruta = calcularReceitaBrutaMensal(mes, ano);
        if (receitaBruta == 0f) {
            proporcao.put("custosFixos", 0f);
            proporcao.put("custosExtras", 0f);
            proporcao.put("lucro", 0f);
            return proporcao;
        }
        Float lucro = receitaBruta - custosFixos - custosExtras;
        proporcao.put("custosFixos", (custosFixos / receitaBruta) * 100);
        proporcao.put("custosExtras", (custosExtras / receitaBruta) * 100);
        proporcao.put("lucro", (lucro / receitaBruta) * 100);
        return proporcao;
    }

    public List<Map<String, Object>> calcularTopServicos(int mes, int ano, int limite) {
        Map<String, Long> contagem = new LinkedHashMap<>();

        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            List<Map<String, Object>> itens = deserializarItens(os.getItensJson());
            for (Map<String, Object> item : itens) {
                String tipo = (String) item.get("tipo");
                if (("SERVICO".equals(tipo) && !isProdutoLegado(item)) || "COMBO".equals(tipo)) {
                    String nome = (String) item.get("nomeItem");
                    int qtd = item.get("quantidade") instanceof Number n ? n.intValue() : 1;
                    contagem.merge(nome, (long) qtd, Long::sum);
                }
            }
        }

        return contagem.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limite)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("nome", e.getKey());
                    m.put("quantidade", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }


    public List<Map<String, Object>> calcularTopProdutos(int mes, int ano, int limite) {
        Map<String, Long> contagem = new LinkedHashMap<>();

        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            List<Map<String, Object>> itens = deserializarItens(os.getItensJson());
            for (Map<String, Object> item : itens) {
                if ("PRODUTO".equals(item.get("tipo")) || isProdutoLegado(item)) {
                    String nome = (String) item.get("nomeItem");
                    if (isProdutoLegado(item)) {
                        nome = "Produto sem nome";
                    }
                    int qtd = item.get("quantidade") instanceof Number n ? n.intValue() : 1;
                    contagem.merge(nome, (long) qtd, Long::sum);
                }
            }
        }

        return contagem.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limite)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("nome", e.getKey());
                    m.put("quantidade", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }


    public Map<String, Long> calcularAtendimentosPorDiaSemana(int mes, int ano) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        resultado.put("Segunda", 0L);
        resultado.put("Terça", 0L);
        resultado.put("Quarta", 0L);
        resultado.put("Quinta", 0L);
        resultado.put("Sexta", 0L);
        resultado.put("Sábado", 0L);
        resultado.put("Domingo", 0L);

        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            if (os.getDiaSemana() != null) {
                resultado.merge(os.getDiaSemana(), 1L, Long::sum);
            }
        }
        return resultado;
    }

    public Map<String, Float> calcularProporcaoAtendimentos(int mes, int ano) {
        Map<String, Integer> contagem = new HashMap<>();
        contagem.put("Serviços", 0);
        contagem.put("Produtos", 0);
        contagem.put("Combos", 0);
        int totalItens = 0;

        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            List<Map<String, Object>> itens = deserializarItens(os.getItensJson());
            for (Map<String, Object> item : itens) {
                int qtd = item.get("quantidade") instanceof Number n ? n.intValue() : 1;
                totalItens += qtd;
                String tipo = (String) item.get("tipo");
                switch (tipo != null ? tipo : "") {
                    case "SERVICO" -> contagem.merge("Serviços", qtd, Integer::sum);
                    case "PRODUTO" -> contagem.merge("Produtos", qtd, Integer::sum);
                    case "COMBO"   -> contagem.merge("Combos", qtd, Integer::sum);
                }
            }
        }

        Map<String, Float> proporcao = new HashMap<>();
        if (totalItens == 0) {
            proporcao.put("Serviços", 0f);
            proporcao.put("Produtos", 0f);
            proporcao.put("Combos", 0f);
            return proporcao;
        }
        int total = totalItens;
        contagem.forEach((k, v) -> proporcao.put(k, ((float) v / total) * 100));
        return proporcao;
    }

    /**
     * Receita gerada por usuário (funcionário) no mês.
     * Retorna mapa usuarioId → valorTotal (nome é resolvido no controller/service).
     */
    public Map<Integer, Float> calcularReceitaPorUsuario(int mes, int ano) {
        Map<Integer, Float> resultado = new HashMap<>();
        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            if (os.getUsuarioId() != null && os.getValorFinal() != null) {
                resultado.merge(os.getUsuarioId(), os.getValorFinal(), Float::sum);
            }
        }
        return resultado;
    }


    public Map<Integer, Long> calcularOrdensPorUsuario(int mes, int ano) {
        Map<Integer, Long> resultado = new HashMap<>();
        for (DashboardOrdemServico os : dashboardRepository.findByMesAndAno(mes, ano)) {
            if (os.getUsuarioId() != null) {
                resultado.merge(os.getUsuarioId(), 1L, Long::sum);
            }
        }
        return resultado;
    }


    public Float calcularComissaoProprietario(int mes, int ano, float custosFixos,
                                               float custosExtras, float percentual) {
        Float receitaLiquida = calcularReceitaLiquidaMensal(mes, ano, custosFixos, custosExtras);
        return receitaLiquida * (percentual / 100);
    }

    private Float arredondar(Float valor) {
        if (valor == null) return 0f;
        return Math.round(valor * 100f) / 100f;
    }

    private String resolverDiaSemana(int dayOfWeek) {
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

    private Float calcularVariacao(Float anterior, Float atual) {
        if (anterior == null || anterior == 0f) {
            return (atual != null && atual > 0) ? 100f : 0f;
        }
        return ((atual - anterior) / anterior) * 100;
    }

    private boolean isProdutoLegado(Map<String, Object> item) {
        Object tipo = item.get("tipo");
        Object nome = item.get("nomeItem");
        return "SERVICO".equals(tipo) && "Serviço #null".equals(nome);
    }


    private String serializarItens(List<OrdemServicoEvent.ItemEvent> itens) {
        if (itens == null || itens.isEmpty()) return "[]";
        try {
            List<Map<String, Object>> lista = itens.stream().map(item -> {
                Map<String, Object> m = new LinkedHashMap<>();
                String tipo;
                String nomeItem;
                if (item.getComboId() != null) {
                    tipo = "COMBO";
                    nomeItem = item.getNomeCombo() != null ? item.getNomeCombo() : "Combo #" + item.getComboId();
                } else if (Boolean.TRUE.equals(item.getEhProduto()) || item.getProdutoId() != null) {
                    tipo = "PRODUTO";
                    nomeItem = item.getNomeServicoProduto() != null
                            ? item.getNomeServicoProduto()
                            : "Produto #" + item.getProdutoId();
                } else {
                    tipo = "SERVICO";
                    nomeItem = item.getNomeServicoProduto() != null
                            ? item.getNomeServicoProduto()
                            : "Serviço #" + item.getServicoProdutoId();
                }
                m.put("nomeItem", nomeItem);
                m.put("tipo", tipo);
                m.put("quantidade", item.getQuantidade() != null ? item.getQuantidade() : 1);
                return m;
            }).collect(Collectors.toList());
            return objectMapper.writeValueAsString(lista);
        } catch (Exception e) {
            log.warn("[DomainService] Erro ao serializar itens: {}", e.getMessage());
            return "[]";
        }
    }

    private List<Map<String, Object>> deserializarItens(String json) {
        if (json == null || json.isBlank() || "[]".equals(json)) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.warn("[DomainService] Erro ao deserializar itens: {}", e.getMessage());
            return List.of();
        }
    }
}
