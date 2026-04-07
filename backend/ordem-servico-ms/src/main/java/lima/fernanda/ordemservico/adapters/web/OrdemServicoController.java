package lima.fernanda.ordemservico.adapters.web;

import jakarta.validation.Valid;
import lima.fernanda.ordemservico.application.dto.OrdemServicoRequest;
import lima.fernanda.ordemservico.application.dto.OrdemServicoResponse;
import lima.fernanda.ordemservico.application.dto.PageResponse;
import lima.fernanda.ordemservico.application.service.OrdemServicoApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoApplicationService service;

    public OrdemServicoController(OrdemServicoApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponse> criar(@RequestBody @Valid OrdemServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrdemServicoResponse>> listar(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.listarPaginado(page));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponse> atualizar(@PathVariable Integer id,
                                                          @RequestBody @Valid OrdemServicoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

