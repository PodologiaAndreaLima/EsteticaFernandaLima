package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.model.Funcionario;
import lima.fernanda.esteticaFernandaLima.service.FuncionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar funcionários", description = "Retorna todos os funcionários")
    @ApiResponse(responseCode = "200", description = "Funcionários encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum funcionário encontrado")
    public ResponseEntity<List<Funcionario>> getFuncionarios() {
        List<Funcionario> funcionarios = service.buscarTodos();
        return funcionarios.isEmpty()?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna um funcionário específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Funcionario> getFuncionarioPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar funcionário", description = "Cadastra um novo funcionário")
    @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso")
    public ResponseEntity<Funcionario> postFuncionario(@RequestBody @Valid Funcionario funcionario) {
        return ResponseEntity.status(201).body(service.salvar(funcionario));
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
    public ResponseEntity<Funcionario> putFuncionario(@PathVariable Integer id, @RequestBody @Valid Funcionario funcionarioAtualizado) {
        try {
            return ResponseEntity.ok(service.atualizar(id, funcionarioAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
