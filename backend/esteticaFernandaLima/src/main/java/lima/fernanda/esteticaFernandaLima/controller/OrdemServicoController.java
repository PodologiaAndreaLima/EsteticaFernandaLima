package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lima.fernanda.esteticaFernandaLima.dto.OrdemServicoRequest;
import lima.fernanda.esteticaFernandaLima.model.OrdemServico;
import lima.fernanda.esteticaFernandaLima.service.OrdemServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordem-servico")
@Tag(name = "Ordem de Serviço", description = "Endpoints para gerenciamento de ordens de serviço")
@SecurityRequirement(name = "Bearer")
public class OrdemServicoController {

    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> getOrdemServico() {
        List<OrdemServico> ordensServico = service.listarTodos();
        return ordensServico.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ordensServico);
    }

    @PostMapping
    public ResponseEntity<OrdemServico> postOrdemServico(@RequestBody OrdemServicoRequest req) {
        OrdemServico saved = service.salvar(req);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServico> atualizarOrdemServico(@PathVariable Integer id,
                                                              @RequestBody OrdemServicoRequest req) {
        OrdemServico updated = service.atualizar(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
