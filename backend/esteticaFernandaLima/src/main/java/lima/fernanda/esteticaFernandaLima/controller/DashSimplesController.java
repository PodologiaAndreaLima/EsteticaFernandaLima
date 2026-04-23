package lima.fernanda.esteticaFernandaLima.controller;

import lima.fernanda.esteticaFernandaLima.dto.ProdutoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.dto.ServicoQuantidadeDTO;
import lima.fernanda.esteticaFernandaLima.service.DashSimplesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dash-simples")
public class DashSimplesController {

    private final DashSimplesService dashSimplesService;

    public DashSimplesController(DashSimplesService dashSimplesService) {
        this.dashSimplesService = dashSimplesService;
    }

    @GetMapping("/renda-bruta-mes-atual")
    public ResponseEntity<Float> getRendaBrutaMesAtual() {
        return ResponseEntity.ok(dashSimplesService.pegarRendaBrutaDoMesAtual());
    }

    @GetMapping("/renda-liquida-mes-atual")
    public ResponseEntity<Float> getRendaLiquidaMesAtual() {
        return ResponseEntity.ok(dashSimplesService.pegarRendaLiquidaDoMesAtual());

    }

    @GetMapping("/renda-bruta-todos-meses")
    public ResponseEntity<Map<Integer, Float>> getRendaBrutaTodosMeses() {
        return ResponseEntity.ok(dashSimplesService.pegarRendaBrutaDeTodosOsMesesDoAnoAtual());
    }

    @GetMapping("/renda-liquida-todos-meses")
    public ResponseEntity<Map<Integer, Float>> getRendaLiquidaTodosMeses() {
        return ResponseEntity.ok(dashSimplesService.pegarRendaLiquidaDeTodosOsMesesDoAnoAtual());
    }

    @GetMapping("/total-ordens-servico")
    public ResponseEntity<Long> getTotalDeOrdensDeServico() {
        return ResponseEntity.ok(dashSimplesService.pegarTotalDeOrdensDeServicoDoMesAtual());
    }

    @GetMapping("/servicos-combos-mais-vendidos-mes")
    public ResponseEntity<List<ServicoQuantidadeDTO>> pegarServicosMaisVendidos() {
        List<ServicoQuantidadeDTO> produtos = dashSimplesService.pegarServicosOuComboMaisVendidosDoMesAtual();
        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos-mais-vendidos-mes")
    public ResponseEntity<List<ProdutoQuantidadeDTO>> pegarProdutosMaisVendidos() {
        List<ProdutoQuantidadeDTO> produtos = dashSimplesService.pegarProdutosMaisVendidosDoMesAtual();

        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/receita-funcionario-mes-atual")
    public ResponseEntity<Float> getReceitaPorFuncionarioMesAtual(
            @RequestParam(required = false) Integer funcionarioId
    ) {
        return ResponseEntity.ok(dashSimplesService.pegarReceitaTotalFuncionarioMesAtual(funcionarioId));
    }

    @GetMapping("/receita-comissao-proprietaria-mes-atual")
    public ResponseEntity<Float> getReceitaComissaoProprietariaMesAtual(
            @RequestParam(defaultValue = "1") Integer proprietarioId
    ) {
        return ResponseEntity.ok(dashSimplesService.pegarReceitaComissionavelProprietariaMesAtual(proprietarioId));
    }

    @GetMapping("/quantidade-ordens-servico-mes-atual-funcionario")
    public ResponseEntity<Long> getQuantidadeOrdensServicoMesAtual(
            @RequestParam(required = false) Integer funcionarioId
    ) {
        return ResponseEntity.ok(dashSimplesService.pegarQuantidadeOrdensFuncionarioMesAtual(funcionarioId));
    }
}

