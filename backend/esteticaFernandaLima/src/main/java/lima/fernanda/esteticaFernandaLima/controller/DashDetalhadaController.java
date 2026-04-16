package lima.fernanda.esteticaFernandaLima.controller;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.service.DashDetalhadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dash-detalhada")
public class DashDetalhadaController {

    private final DashDetalhadaService dashDetalhadaService;

    public DashDetalhadaController(DashDetalhadaService dashDetalhadaService) {
        this.dashDetalhadaService = dashDetalhadaService;
    }

    @GetMapping("/renda-bruta-mes-atual")
    public ResponseEntity<Float> getRendaBrutaMesAtual() {
        return ResponseEntity.ok(dashDetalhadaService.pegarRendaBrutaDoMesAtual());
    }

    @GetMapping("/renda-liquida-mes-atual")
    public ResponseEntity<Float> getRendaLiquidaMesAtual() {
        return ResponseEntity.ok(dashDetalhadaService.pegarRendaLiquidaDoMesAtual());

    }

    @GetMapping("/renda-bruta-todos-meses")
    public ResponseEntity<Map<Integer, Float>> getRendaBrutaTodosMeses() {
        return ResponseEntity.ok(dashDetalhadaService.pegarRendaBrutaDeTodosOsMesesDoAnoAtual());
    }

    @GetMapping("/renda-liquida-todos-meses")
    public ResponseEntity<Map<Integer, Float>> getRendaLiquidaTodosMeses() {
        return ResponseEntity.ok(dashDetalhadaService.pegarRendaLiquidaDeTodosOsMesesDoAnoAtual());
    }

    @GetMapping("/total-ordens-servico")
    public ResponseEntity<Long> getTotalDeOrdensDeServico() {
        return ResponseEntity.ok(dashDetalhadaService.pegarTotalDeOrdensDeServicoDoMesAtual());
    }

    @GetMapping("/servicos-combos-mais-vendidos-mes")
    public ResponseEntity<List<ServicoQuantidadeDTO>> pegarServicosMaisVendidos() {
        List<ServicoQuantidadeDTO> produtos = dashDetalhadaService.pegarServicosOuComboMaisVendidosDoMesAtual();
        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos-mais-vendidos-mes")
    public ResponseEntity<List<ProdutoQuantidadeDTO>> pegarProdutosMaisVendidos() {
        List<ProdutoQuantidadeDTO> produtos = dashDetalhadaService.pegarProdutosMaisVendidosDoMesAtual();

        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/receita-funcionario-mes-atual")
    public ResponseEntity<Float> getReceitaPorFuncionarioMesAtual() {
        return ResponseEntity.ok(dashDetalhadaService.pegarReceitaTotalFuncionarioMesAtual());
    }

    @GetMapping("/quantidade-ordens-servico-mes-atual-funcionario")
    public ResponseEntity<Long> getQuantidadeOrdensServicoMesAtual() {
        return ResponseEntity.ok(dashDetalhadaService.pegarQuantidadeOrdensFuncionarioMesAtual());
    }

    // Novos endpoints para dashboard detalhada com parâmetros mes e ano
    @GetMapping("/receita-bruta-mensal/{mes}/{ano}")
    public ResponseEntity<Float> getReceitaBrutaMensal(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getReceitaBrutaMensal(mes, ano));
    }

    @GetMapping("/receita-liquida-mensal/{mes}/{ano}")
    public ResponseEntity<Float> getReceitaLiquidaMensal(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getReceitaLiquidaMensal(mes, ano));
    }

    @GetMapping("/custos-fixos-mensais/{mes}/{ano}")
    public ResponseEntity<Float> getCustosFixosMensais(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getCustosFixosMensais(mes, ano));
    }

    @GetMapping("/custos-extras-mensais/{mes}/{ano}")
    public ResponseEntity<Float> getCustosExtrasMensais(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getCustosExtrasMensais(mes, ano));
    }

    @GetMapping("/margem-lucro-bruto/{mes}/{ano}")
    public ResponseEntity<Float> getMargemLucroBruto(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getMargemLucroBruto(mes, ano));
    }

    @GetMapping("/variacao-mensal/{mesAtual}/{anoAtual}")
    public ResponseEntity<Map<String, Float>> getVariacaoMensal(@PathVariable int mesAtual, @PathVariable int anoAtual) {
        return ResponseEntity.ok(dashDetalhadaService.getVariacaoMensal(mesAtual, anoAtual));
    }

    @GetMapping("/receita-ao-longo-dos-meses/{ano}")
    public ResponseEntity<Map<String, List<Float>>> getReceitaAoLongoDosMeses(@PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getReceitaAoLongoDosMeses(ano));
    }

    @GetMapping("/proporcao-custos/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getProporcaoCustos(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getProporcaoCustos(mes, ano));
    }

    @GetMapping("/numero-ordens-mensal/{mes}/{ano}")
    public ResponseEntity<Long> getNumeroOrdensMensal(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getNumeroOrdensMensal(mes, ano));
    }

    @GetMapping("/ticket-medio/{mes}/{ano}")
    public ResponseEntity<Float> getTicketMedio(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getTicketMedio(mes, ano));
    }

    @GetMapping("/custo-medio-por-servico/{mes}/{ano}")
    public ResponseEntity<Float> getCustoMedioPorServico(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getCustoMedioPorServico(mes, ano));
    }

    @GetMapping("/top-servicos/{mes}/{ano}/{limite}")
    public ResponseEntity<List<ServicoQuantidadeDTO>> getTopServicos(@PathVariable int mes, @PathVariable int ano, @PathVariable int limite) {
        List<ServicoQuantidadeDTO> servicos = dashDetalhadaService.getTopServicos(mes, ano, limite);
        if (servicos == null || servicos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/top-produtos/{mes}/{ano}/{limite}")
    public ResponseEntity<List<ProdutoQuantidadeDTO>> getTopProdutos(@PathVariable int mes, @PathVariable int ano, @PathVariable int limite) {
        List<ProdutoQuantidadeDTO> produtos = dashDetalhadaService.getTopProdutos(mes, ano, limite);
        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/atendimentos-por-dia-semana/{mes}/{ano}")
    public ResponseEntity<Map<String, Long>> getAtendimentosPorDiaSemana(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getAtendimentosPorDiaSemana(mes, ano));
    }

    @GetMapping("/proporcao-atendimentos/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getProporcaoAtendimentos(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getProporcaoAtendimentos(mes, ano));
    }

    @GetMapping("/receita-por-funcionario/{mes}/{ano}")
    public ResponseEntity<Map<String, Float>> getReceitaPorFuncionario(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getReceitaPorFuncionario(mes, ano));
    }

    @GetMapping("/comissao-proprietario/{mes}/{ano}/{percentual}")
    public ResponseEntity<Float> getComissaoProprietario(@PathVariable int mes, @PathVariable int ano, @PathVariable float percentual) {
        return ResponseEntity.ok(dashDetalhadaService.getComissaoProprietario(mes, ano, percentual));
    }

    @GetMapping("/ordens-por-funcionario/{mes}/{ano}")
    public ResponseEntity<Map<String, Long>> getOrdensPorFuncionario(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getOrdensPorFuncionario(mes, ano));
    }

    @GetMapping("/atendimentos-funcionario-por-dia-semana/{mes}/{ano}")
    public ResponseEntity<Map<String, Long>> getAtendimentosFuncionarioPorDiaSemana(@PathVariable int mes, @PathVariable int ano) {
        return ResponseEntity.ok(dashDetalhadaService.getAtendimentosFuncionarioPorDiaSemana(mes, ano));
    }
}
