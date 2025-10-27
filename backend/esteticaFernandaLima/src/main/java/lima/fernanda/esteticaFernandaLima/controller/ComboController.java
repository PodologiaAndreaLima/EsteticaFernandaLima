package lima.fernanda.esteticaFernandaLima.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import lima.fernanda.esteticaFernandaLima.model.Combo;
import lima.fernanda.esteticaFernandaLima.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/combo")
@Tag(name = "Combos", description = "Endpoints para gerenciamento de combos")
@SecurityRequirement(name = "Bearer")
public class ComboController {

    private final ComboService service;

    public ComboController(ComboService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar combos", description = "Retorna todos os combos")
    @ApiResponse(responseCode = "200", description = "Combos encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum combo encontrado")
    public ResponseEntity<java.util.List<Combo>> getCombos() {
        java.util.List<Combo> combos = service.buscarTodos();
        return combos.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(combos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combo por ID", description = "Retorna um combo específico pelo ID")
    public ResponseEntity<Combo> getComboPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar combo", description = "Cadastra um novo combo")
    public ResponseEntity<Combo> postCombo(@RequestBody Combo combo) {
        return ResponseEntity.status(201).body(service.salvar(combo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir combo", description = "Remove um combo pelo ID")
    @ApiResponse(responseCode = "204", description = "Combo removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Combo não encontrado")
    public ResponseEntity<Void> deleteComboPorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combo", description = "Atualiza um combo existente pelo ID")
    public ResponseEntity<Combo> putCombo(@PathVariable Integer id, @RequestBody Combo comboAtualizado) {
        try {
            Combo comboAtual = service.atualizar(id, comboAtualizado);
            return ResponseEntity.ok(comboAtual);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
