package lima.fernanda.esteticaFernandaLima.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "Listar ordens de serviço", description = "Retorna todas as ordens de serviço")
    @ApiResponse(responseCode = "200", description = "Ordens de serviço encontradas")
    @ApiResponse(responseCode = "204", description = "Nenhuma ordem de serviço encontrada")
    public ResponseEntity<List<OrdemServico>> getOrdemServico() {
        List<OrdemServico> ordensServico = service.listarTodos();
        return ordensServico.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(ordensServico);
    }


    @PostMapping
    @Operation(summary = "Cadastrar ordem de serviço", description = "Cadastra uma nova ordem de serviço")
    @ApiResponse(responseCode = "201", description = "Ordem de serviço cadastrada com sucesso")
    public ResponseEntity<OrdemServico> postOrdemServico(@RequestBody OrdemServico ordemServico) {
        return ResponseEntity.status(201).body(service.salvar(ordemServico));

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir ordem de serviço", description = "Remove uma ordem de serviço pelo ID")
    @ApiResponse(responseCode = "204", description = "Ordem de serviço removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    public ResponseEntity<Void> deleteOrdemServicoPorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ordem de serviço", description = "Atualiza uma ordem de serviço pelo ID")
    @ApiResponse(responseCode = "200", description = "Ordem de serviço atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada")
    public ResponseEntity<OrdemServico> atualizarOrdemServico(@PathVariable Integer id, @RequestBody OrdemServico ordemServicoAtualizada) {
        try {
            OrdemServico ordemServicoAtualizadaResult = service.atualizar(id, ordemServicoAtualizada);
            return ResponseEntity.ok(ordemServicoAtualizadaResult);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
