package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.FuncionarioResponse;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.service.FuncionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Endpoints para gerenciamento de funcionários")
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasRole('ADMIN')")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar funcionários", description = "Retorna todos os funcionários")
    @ApiResponse(responseCode = "200", description = "Funcionários encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum funcionário encontrado")
    public ResponseEntity<List<FuncionarioResponse>> getFuncionarios() {
        List<Funcionario> funcionarios = service.buscarTodos();
        if (funcionarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<FuncionarioResponse> resposta = funcionarios.stream()
                .map(FuncionarioResponse::fromFuncionario)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna um funcionário específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioResponse> getFuncionarioPorId(@PathVariable Integer id) {
        try {
            Funcionario funcionario = service.buscarPorId(id);
            return ResponseEntity.ok(FuncionarioResponse.fromFuncionario(funcionario));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar funcionário", description = "Cadastra um novo funcionário")
    @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso")
    public ResponseEntity<FuncionarioResponse> postFuncionario(@RequestBody @Valid FuncionarioCriacaoDto funcionario) {
        Funcionario salvo = service.salvar(funcionario);
        return ResponseEntity.status(201).body(FuncionarioResponse.fromFuncionario(salvo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir funcionário", description = "Remove um funcionário pelo ID")
    @ApiResponse(responseCode = "204", description = "Funcionário removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Void> deleteFuncionarioPorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário existente")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioResponse> putFuncionario(@PathVariable Integer id, @RequestBody @Valid FuncionarioAtualizacaoDto funcionarioAtualizado) {
        try {
            Funcionario atualizado = service.atualizar(id, funcionarioAtualizado);
            return ResponseEntity.ok(FuncionarioResponse.fromFuncionario(atualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
