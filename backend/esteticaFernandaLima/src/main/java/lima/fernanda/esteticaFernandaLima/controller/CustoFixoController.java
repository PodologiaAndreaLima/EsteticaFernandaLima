package lima.fernanda.esteticaFernandaLima.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lima.fernanda.esteticaFernandaLima.model.CustoFixo;
import lima.fernanda.esteticaFernandaLima.service.CustoFixoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custos-fixos")
@Tag(name = "Custos Fixos", description = "Endpoints para gerenciamento de custos fixos")
@SecurityRequirement(name = "Bearer")
public class CustoFixoController {

    private final CustoFixoService service;

    public CustoFixoController(CustoFixoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar custos fixos", description = "Retorna todos os custos fixos cadastrados")
    @ApiResponse(responseCode = "200", description = "Custos fixos encontrados")
    public ResponseEntity<List<CustoFixo>> listarTodos() {
        List<CustoFixo> custosFixos = service.listarTodos();
        return ResponseEntity.ok(custosFixos);
    }

    @PostMapping
    @Operation(summary = "Cadastrar custo fixo", description = "Cadastra um novo custo fixo")
    @ApiResponse(responseCode = "201", description = "Custo fixo cadastrado com sucesso")
    public ResponseEntity<CustoFixo> postCustoFixo(@RequestBody CustoFixo custoFixo) {
        CustoFixo novoCustoFixo = service.salvar(custoFixo);
        return ResponseEntity.status(201).body(novoCustoFixo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir custo fixo", description= "Remove um custo fixo pelo ID")
    @ApiResponse(responseCode = "204", description = "Custo fixo removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Custo fixo não encontrado")
    public ResponseEntity<Void> deleteCustoFixoPorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar custo fixo", description = "Atualiza os dados de um custo fixo pelo ID")
    @ApiResponse(responseCode = "200", description = "Custo fixo atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Custo fixo não encontrado")
    public ResponseEntity<CustoFixo> putCustoFixo(@PathVariable Integer id, @RequestBody CustoFixo custoFixoAtualizado) {
        try {
            CustoFixo custoFixoAtualizadoResult = service.atualizar(id, custoFixoAtualizado);
            return ResponseEntity.ok(custoFixoAtualizadoResult);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
