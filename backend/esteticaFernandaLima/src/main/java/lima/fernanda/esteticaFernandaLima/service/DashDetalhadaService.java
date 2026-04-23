package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.integration.ordemservico.OrdemServicoMsClient;
import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.repository.CustoExtraRepository;
import lima.fernanda.esteticaFernandaLima.repository.FuncionarioRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsabilidades após a migração para processamento assíncrono:
 *  1. Calcular/fornecer os custos (fixos e extras) — estes dados residem aqui.
 *  2. Delegar TODOS os cálculos sobre ordens de serviço ao ms-scr via REST,
 *     passando os custos como parâmetros.
 *  3. Resolver nomes de funcionários (usuários) — tabela de funcionários está aqui.
 * O processamento pesado (receita, top itens, proporções) não ocorre mais neste serviço.
 */
@Service
public class DashDetalhadaService {

    private static final int FUNCIONARIO_ID_FIXO = 2;

    private final OrdemServicoMsClient msClient;
    private final CustoExtraRepository custoExtraRepository;
    private final CustoFixoService custoFixoService;
    private final FuncionarioRepository funcionarioRepository;
    private final RestTemplate restTemplate;
    private final String msBaseUrl;

    public DashDetalhadaService(OrdemServicoMsClient msClient,
                                 CustoExtraRepository custoExtraRepository,
                                 CustoFixoService custoFixoService,
                                 FuncionarioRepository funcionarioRepository,
                                 @Value("${ordemservico.ms.base-url:http://localhost:8081}") String msBaseUrl) {
        this.msClient = msClient;
        this.custoExtraRepository = custoExtraRepository;
        this.custoFixoService = custoFixoService;
        this.funcionarioRepository = funcionarioRepository;
        this.restTemplate = new RestTemplate();
        this.msBaseUrl = msBaseUrl;
    }

    // Custos

    public Float getCustosFixosMensais(int mes, int ano) {
        return custoFixoService.listarTodos().stream()
                .map(CustoFixo::getValorMensal)
                .filter(Objects::nonNull)
                .reduce(0.0F, Float::sum);
    }

    public Float getCustosExtrasMensais(int mes, int ano) {
        return custoExtraRepository.findValoresByAnoEMes(ano, mes).stream()
                .filter(Objects::nonNull)
                .reduce(0.0F, Float::sum);
    }

    // Receita (delegado ao ms-scr com custos como parâmetro)

    public Float getReceitaBrutaMensal(int mes, int ano) {
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/receita-bruta/{mes}/{ano}",
                Float.class, mes, ano);
        return resultado != null ? resultado : 0f;
    }

    public Float getReceitaLiquidaMensal(int mes, int ano) {
        float cf = getCustosFixosMensais(mes, ano);
        float ce = getCustosExtrasMensais(mes, ano);
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/receita-liquida/{mes}/{ano}?custosFixos={cf}&custosExtras={ce}",
                Float.class, mes, ano, cf, ce);
        return resultado != null ? resultado : 0f;
    }

    public Float getMargemLucroBruto(int mes, int ano) {
        float cf = getCustosFixosMensais(mes, ano);
        float ce = getCustosExtrasMensais(mes, ano);
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/margem-lucro/{mes}/{ano}?custosFixos={cf}&custosExtras={ce}",
                Float.class, mes, ano, cf, ce);
        return resultado != null ? resultado : 0f;
    }

    public Long getNumeroOrdensMensal(int mes, int ano) {
        Long resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/numero-ordens/{mes}/{ano}",
                Long.class, mes, ano);
        return resultado != null ? resultado : 0L;
    }

    public Float getTicketMedio(int mes, int ano) {
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/ticket-medio/{mes}/{ano}",
                Float.class, mes, ano);
        return resultado != null ? resultado : 0f;
    }

    public Float getCustoMedioPorServico(int mes, int ano) {
        float cf = getCustosFixosMensais(mes, ano);
        float ce = getCustosExtrasMensais(mes, ano);
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/custo-medio-servico/{mes}/{ano}?custosFixos={cf}&custosExtras={ce}",
                Float.class, mes, ano, cf, ce);
        return resultado != null ? resultado : 0f;
    }

    public Map<String, Float> getVariacaoMensal(int mesAtual, int anoAtual) {
        int mesAnterior = mesAtual == 1 ? 12 : mesAtual - 1;
        int anoAnterior = mesAtual == 1 ? anoAtual - 1 : anoAtual;

        float cfAtual = getCustosFixosMensais(mesAtual, anoAtual);
        float ceAtual = getCustosExtrasMensais(mesAtual, anoAtual);
        float cfAnterior = getCustosFixosMensais(mesAnterior, anoAnterior);
        float ceAnterior = getCustosExtrasMensais(mesAnterior, anoAnterior);

        String url = msBaseUrl + "/dashboard/variacao-mensal/{mes}/{ano}" +
                "?custosFixosAtual={cfa}&custosExtrasAtual={cea}" +
                "&custosFixosAnterior={cfan}&custosExtrasAnterior={cean}";

        Map<String, Float> resultado = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Float>>() {},
                mesAtual, anoAtual, cfAtual, ceAtual, cfAnterior, ceAnterior
        ).getBody();

        return resultado != null ? resultado : Map.of("variacaoReceita", 0f, "variacaoCustos", 0f);
    }

    public Map<String, List<Float>> getReceitaAoLongoDosMeses(int ano) {
        // Monta mapa de custos por mês e envia ao ms-scr
        Map<Integer, Float> custosFixosPorMes = new HashMap<>();
        Map<Integer, Float> custosExtrasPorMes = new HashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            custosFixosPorMes.put(mes, getCustosFixosMensais(mes, ano));
            custosExtrasPorMes.put(mes, getCustosExtrasMensais(mes, ano));
        }
        Map<String, Map<Integer, Float>> body = Map.of(
                "custosFixos", custosFixosPorMes,
                "custosExtras", custosExtrasPorMes
        );

        Map<String, List<Float>> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/receita-anual/{ano}",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(body),
                new ParameterizedTypeReference<Map<String, List<Float>>>() {},
                ano
        ).getBody();

        return resultado != null ? resultado : Map.of("receitaBruta", List.of(), "receitaLiquida", List.of());
    }

    public Map<String, Float> getProporcaoCustos(int mes, int ano) {
        float cf = getCustosFixosMensais(mes, ano);
        float ce = getCustosExtrasMensais(mes, ano);
        Map<String, Float> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/proporcao-custos/{mes}/{ano}?custosFixos={cf}&custosExtras={ce}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Float>>() {},
                mes, ano, cf, ce
        ).getBody();
        return resultado != null ? resultado : Map.of();
    }

    @SuppressWarnings("unchecked")
    public List<ServicoQuantidadeDTO> getTopServicos(int mes, int ano, int limite) {
        List<Map<String, Object>> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/top-servicos/{mes}/{ano}?limite={limite}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {},
                mes, ano, limite
        ).getBody();

        if (resultado == null) return List.of();
        return resultado.stream()
                .map(m -> new ServicoQuantidadeDTO(
                        (String) m.get("nome"),
                        ((Number) m.get("quantidade")).longValue()))
                .collect(Collectors.toList());
    }

    public List<ProdutoQuantidadeDTO> getTopProdutos(int mes, int ano, int limite) {
        List<Map<String, Object>> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/top-produtos/{mes}/{ano}?limite={limite}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {},
                mes, ano, limite
        ).getBody();

        if (resultado == null) return List.of();
        return resultado.stream()
                .map(m -> new ProdutoQuantidadeDTO(
                        (String) m.get("nome"),
                        ((Number) m.get("quantidade")).longValue()))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getAtendimentosPorDiaSemana(int mes, int ano) {
        Map<String, Long> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/atendimentos-dia-semana/{mes}/{ano}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Long>>() {},
                mes, ano
        ).getBody();
        return resultado != null ? resultado : Map.of();
    }

    public Map<String, Float> getProporcaoAtendimentos(int mes, int ano) {
        Map<String, Float> resultado = restTemplate.exchange(
                msBaseUrl + "/dashboard/proporcao-atendimentos/{mes}/{ano}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Float>>() {},
                mes, ano
        ).getBody();
        return resultado != null ? resultado : Map.of();
    }

    public Map<String, Float> getReceitaPorFuncionario(int mes, int ano) {
        // Busca mapa usuarioId→receita do ms-scr e resolve nomes localmente
        Map<Integer, Float> porUsuario = restTemplate.exchange(
                msBaseUrl + "/dashboard/receita-por-usuario/{mes}/{ano}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<Integer, Float>>() {},
                mes, ano
        ).getBody();

        if (porUsuario == null) return Map.of();
        Map<String, Float> resultado = new HashMap<>();
        porUsuario.forEach((usuarioId, receita) -> {
            String nome = funcionarioRepository.findById(usuarioId)
                    .map(Funcionario::getNome)
                    .orElse("Funcionário #" + usuarioId);
            resultado.put(nome, receita);
        });
        return resultado;
    }

    public Map<String, Long> getOrdensPorFuncionario(int mes, int ano) {
        Map<Integer, Long> porUsuario = restTemplate.exchange(
                msBaseUrl + "/dashboard/ordens-por-usuario/{mes}/{ano}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<Integer, Long>>() {},
                mes, ano
        ).getBody();

        if (porUsuario == null) return Map.of();
        Map<String, Long> resultado = new HashMap<>();
        porUsuario.forEach((usuarioId, qtd) -> {
            String nome = funcionarioRepository.findById(usuarioId)
                    .map(Funcionario::getNome)
                    .orElse("Funcionário #" + usuarioId);
            resultado.put(nome, qtd);
        });
        return resultado;
    }

    public Float getComissaoProprietario(int mes, int ano, float percentual) {
        float cf = getCustosFixosMensais(mes, ano);
        float ce = getCustosExtrasMensais(mes, ano);
        Float resultado = restTemplate.getForObject(
                msBaseUrl + "/dashboard/comissao-proprietario/{mes}/{ano}/{percentual}?custosFixos={cf}&custosExtras={ce}",
                Float.class, mes, ano, percentual, cf, ce);
        return resultado != null ? resultado : 0f;
    }

    public Map<String, Long> getAtendimentosFuncionarioPorDiaSemana(int mes, int ano) {
        return getAtendimentosPorDiaSemana(mes, ano);
    }

    // Métodos de compatibilidade (mês atual)

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
        for (int mes = 1; mes <= 12; mes++) map.put(mes, getReceitaBrutaMensal(mes, ano));
        return map;
    }

    public Map<Integer, Float> pegarRendaLiquidaDeTodosOsMesesDoAnoAtual() {
        int ano = LocalDate.now().getYear();
        Map<Integer, Float> map = new HashMap<>();
        for (int mes = 1; mes <= 12; mes++) map.put(mes, getReceitaLiquidaMensal(mes, ano));
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
        Map<String, Float> receita = getReceitaPorFuncionario(hoje.getMonthValue(), hoje.getYear());
        return receita.getOrDefault("Funcionário #" + FUNCIONARIO_ID_FIXO, 0.0F);
    }

    public Long pegarQuantidadeOrdensFuncionarioMesAtual() {
        LocalDate hoje = LocalDate.now();
        Map<String, Long> ordens = getOrdensPorFuncionario(hoje.getMonthValue(), hoje.getYear());
        return ordens.getOrDefault("Funcionário #" + FUNCIONARIO_ID_FIXO, 0L);
    }
}
