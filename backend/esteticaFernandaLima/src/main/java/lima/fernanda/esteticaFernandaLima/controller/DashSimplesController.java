package lima.fernanda.esteticaFernandaLima.controller;

import lima.fernanda.esteticaFernandaLima.service.DashSimplesService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//        try {
//            return ResponseEntity.ok(dashSimplesService.pegarRendaLiquidaDoMesAtual());
//        } catch (Exception e) {
//            throw new RuntimeException("Erro ao obter a renda líquida do mês atual.");
//        }
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
}
