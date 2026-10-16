package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.dto.ComboDetalheResponse;
import lima.fernanda.esteticaFernandaLima.dto.ComboRequest;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "Listar combos",
            description = "Filtra por status (ATIVO, INATIVO ou TODOS). Sem o parâmetro, retorna apenas os ativos")
    @ApiResponse(responseCode = "200", description = "Combos encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum combo encontrado")
    public ResponseEntity<List<ComboDetalheResponse>> getCombos(
            @RequestParam(defaultValue = "ATIVO") StatusCadastro status) {
        List<ComboDetalheResponse> combos = service.listarPorStatus(status);
        return combos.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(combos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar combo por ID", description = "Retorna um combo com seus serviços e produtos")
    @ApiResponse(responseCode = "404", description = "Combo não encontrado")
    public ResponseEntity<ComboDetalheResponse> getComboPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.detalhar(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar combo",
            description = "Cadastra um combo (sempre ativo). Se enviar idsServicos, exige ao menos 1 serviço ativo")
    public ResponseEntity<ComboDetalheResponse> postCombo(@RequestBody @Valid ComboRequest request) {
        return ResponseEntity.status(201).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar combo",
            description = "Atualiza um combo. Sem idsServicos/idsProdutos, mantém os itens atuais")
    @ApiResponse(responseCode = "404", description = "Combo não encontrado")
    public ResponseEntity<ComboDetalheResponse> putCombo(@PathVariable Integer id,
                                                         @RequestBody @Valid ComboRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar combo",
            description = "Exclusão lógica: marca como inativo e mantém o histórico de vendas e dashboards")
    @ApiResponse(responseCode = "204", description = "Combo inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Combo não encontrado")
    public ResponseEntity<Void> inativarCombo(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar combo", description = "Volta um combo inativo para ativo")
    @ApiResponse(responseCode = "404", description = "Combo não encontrado")
    public ResponseEntity<ComboDetalheResponse> reativarCombo(@PathVariable Integer id) {
        return ResponseEntity.ok(service.reativar(id));
    }
}