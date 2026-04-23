package lima.fernanda.ordemservico.adapters.web;

import lima.fernanda.ordemservico.application.service.DashboardApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


//  Controller REST que expõe as métricas pré-calculadas da dashboard.
//  Parâmetros de custo (custosFixos, custosExtras) são recebidos via query param porque vivem no backend
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class DashboardController {

    private final DashboardApplicationService dashboardService;

    public DashboardController(DashboardApplicationService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Receita

    @GetMapping("/receita-bruta/{mes}/{ano}")
    public ResponseEntity<Float> getReceitaBruta(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getReceitaBrutaMensal(mes, ano));
    }

    @GetMapping("/receita-liquida/{mes}/{ano}")
    public ResponseEntity<Float> getReceitaLiquida(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "0") float custosFixos,
            @RequestParam(defaultValue = "0") float custosExtras) {
        return ResponseEntity.ok(dashboardService.getReceitaLiquidaMensal(mes, ano, custosFixos, custosExtras));
    }

    @GetMapping("/margem-lucro/{mes}/{ano}")
    public ResponseEntity<Float> getMargemLucro(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "0") float custosFixos,
            @RequestParam(defaultValue = "0") float custosExtras) {
        return ResponseEntity.ok(dashboardService.getMargemLucroBruto(mes, ano, custosFixos, custosExtras));
    }

    // Ordens

    @GetMapping("/numero-ordens/{mes}/{ano}")
    public ResponseEntity<Long> getNumeroOrdens(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getNumeroOrdensMensal(mes, ano));
    }

    @GetMapping("/ticket-medio/{mes}/{ano}")
    public ResponseEntity<Float> getTicketMedio(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getTicketMedio(mes, ano));
    }

    @GetMapping("/custo-medio-servico/{mes}/{ano}")
    public ResponseEntity<Float> getCustoMedioServico(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "0") float custosFixos,
            @RequestParam(defaultValue = "0") float custosExtras) {
        return ResponseEntity.ok(dashboardService.getCustoMedioPorServico(mes, ano, custosFixos, custosExtras));
    }

    //  Variação e séries temporais

    /**
     * Variação percentual de receita e custos em relação ao mês anterior.
     * Requer os custos do mês atual e do mês anterior como parâmetros.
     */
    @GetMapping("/variacao-mensal/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getVariacaoMensal(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "0") float custosFixosAtual,
            @RequestParam(defaultValue = "0") float custosExtrasAtual,
            @RequestParam(defaultValue = "0") float custosFixosAnterior,
            @RequestParam(defaultValue = "0") float custosExtrasAnterior) {
        return ResponseEntity.ok(dashboardService.getVariacaoMensal(
                mes, ano,
                custosFixosAtual, custosExtrasAtual,
                custosFixosAnterior, custosExtrasAnterior));
    }

    /**
     * Receita bruta e líquida mês a mês de um ano.
     * Custos mensais passados como JSON body para suportar valores distintos por mês.
     */
    @PostMapping("/receita-anual/{ano}")
    public ResponseEntity<Map<String, List<Float>>> getReceitaAnual(
            @PathVariable int ano,
            @RequestBody Map<String, Map<Integer, Float>> custosPorMes) {
        Map<Integer, Float> custosFixos = custosPorMes.getOrDefault("custosFixos", Map.of());
        Map<Integer, Float> custosExtras = custosPorMes.getOrDefault("custosExtras", Map.of());
        return ResponseEntity.ok(dashboardService.getReceitaAoLongoDosMeses(ano, custosFixos, custosExtras));
    }

    @GetMapping("/proporcao-custos/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getProporcaoCustos(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "0") float custosFixos,
            @RequestParam(defaultValue = "0") float custosExtras) {
        return ResponseEntity.ok(dashboardService.getProporcaoCustos(mes, ano, custosFixos, custosExtras));
    }

    // Top itens

    @GetMapping("/top-servicos/{mes}/{ano}")
    public ResponseEntity<List<Map<String, Object>>> getTopServicos(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "5") int limite) {
        List<Map<String, Object>> resultado = dashboardService.getTopServicos(mes, ano, limite);
        return resultado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resultado);
    }

    @GetMapping("/top-produtos/{mes}/{ano}")
    public ResponseEntity<List<Map<String, Object>>> getTopProdutos(
            @PathVariable int mes, @PathVariable int ano,
            @RequestParam(defaultValue = "5") int limite) {
        List<Map<String, Object>> resultado = dashboardService.getTopProdutos(mes, ano, limite);
        return resultado.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resultado);
    }

    // Distribuição

    @GetMapping("/atendimentos-dia-semana/{mes}/{ano}")
    public ResponseEntity<Map<String, Long>> getAtendimentosPorDiaSemana(
            @PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getAtendimentosPorDiaSemana(mes, ano));
    }

    @GetMapping("/proporcao-atendimentos/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getProporcaoAtendimentos(
            @PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getProporcaoAtendimentos(mes, ano));
    }

    // Por usuário/funcionário

    @GetMapping("/receita-por-usuario/{mes}/{ano}")
    public ResponseEntity<Map<Integer, Float>> getReceitaPorUsuario(
            @PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getReceitaPorUsuario(mes, ano));
    }

    @GetMapping("/ordens-por-usuario/{mes}/{ano}")
    public ResponseEntity<Map<Integer, Long>> getOrdensPorUsuario(
            @PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashboardService.getOrdensPorUsuario(mes, ano));
    }

    @GetMapping("/comissao-proprietario/{mes}/{ano}/{percentual}")
    public ResponseEntity<Float> getComissaoProprietario(
            @PathVariable int mes, @PathVariable int ano, @PathVariable float percentual,
            @RequestParam(defaultValue = "0") float custosFixos,
            @RequestParam(defaultValue = "0") float custosExtras) {
        return ResponseEntity.ok(
                dashboardService.getComissaoProprietario(mes, ano, custosFixos, custosExtras, percentual));
    }
}
