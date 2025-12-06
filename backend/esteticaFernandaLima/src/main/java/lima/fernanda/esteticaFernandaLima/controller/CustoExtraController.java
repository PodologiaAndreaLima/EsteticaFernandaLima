package lima.fernanda.esteticaFernandaLima.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.model.CustoExtra;
import lima.fernanda.esteticaFernandaLima.service.CustoExtraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custos-extras")
@Tag(name = "Custos Extras", description = "Endpoints para gerenciamento de custos extras")
@SecurityRequirement(name = "Bearer")
public class CustoExtraController {

    private final CustoExtraService service;

    public CustoExtraController(CustoExtraService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar custos extras", description = "Retorna todos os custos extras cadastrados")
    @ApiResponse(responseCode = "200", description = "Custos extras encontrados")
    public ResponseEntity<List<CustoExtra>> listarTodos() {
        List<CustoExtra> custosExtras = service.listarTodos();
        return ResponseEntity.ok(custosExtras);
    }

    @PostMapping
    @Operation(summary = "Cadastrar custo extra", description = "Cadastra um novo custo extra")
    @ApiResponse(responseCode = "201", description = "Custo extra cadastrado com sucesso")
    public ResponseEntity<CustoExtra> criarCustoExtra(@RequestBody @Valid CustoExtra custoExtra) {
        CustoExtra novoCustoExtra = service.salvar(custoExtra);
        return ResponseEntity.status(201).body(novoCustoExtra);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir custo extra", description = "Remove um custo extra pelo ID")
    @ApiResponse(responseCode = "204", description = "Custo extra removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Custo extra não encontrado")
    public ResponseEntity<Void> deletarCustoExtra(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar custo extra", description = "Atualiza um custo extra existente pelo ID")
    @ApiResponse(responseCode = "200", description = "Custo extra atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Custo extra não encontrado")
    public ResponseEntity<CustoExtra> atualizarCustoExtra(@PathVariable Integer id, @RequestBody @Valid CustoExtra custoExtra) {
        try {
            CustoExtra custoExtraAtualizado = service.atualizar(id, custoExtra);
            return ResponseEntity.ok(custoExtraAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
