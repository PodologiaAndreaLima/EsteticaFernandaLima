package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.enums.StatusServicoProduto;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.service.ServicoProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico-produto")
@Tag(name = "Serviços/Produtos", description = "Endpoints para gerenciamento de serviços e produtos")
@SecurityRequirement(name = "Bearer")
public class ServicoProdutoController {

    private final ServicoProdutoService service;

    public ServicoProdutoController(ServicoProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar serviços/produtos",
            description = "Filtra por status (ATIVO, INATIVO ou TODOS). Sem o parâmetro, retorna apenas os ativos")
    @ApiResponse(responseCode = "200", description = "Serviços/produtos encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum serviço/produto encontrado")
    public ResponseEntity<List<ServicoProduto>> getServicoProduto(
            @RequestParam(defaultValue = "ATIVO") StatusServicoProduto status) {
        List<ServicoProduto> servicoProdutos = service.buscarPorStatus(status);
        return servicoProdutos.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(servicoProdutos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço/produto por ID", description = "Retorna um serviço/produto específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Serviço/produto encontrado")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<ServicoProduto> getServicoProdutoPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto ou serviço", description = "Cadastra um novo produto ou serviço (sempre ativo)")
    @ApiResponse(responseCode = "201", description = "Produto/serviço cadastrado com sucesso")
    public ResponseEntity<ServicoProduto> postServicoProduto(@RequestBody @Valid ServicoProduto servicoProduto) {
        return ResponseEntity.status(201).body(service.salvar(servicoProduto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço/produto", description = "Atualiza um serviço/produto pelo ID")
    @ApiResponse(responseCode = "200", description = "Serviço/produto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<ServicoProduto> atualizarServicoProduto(@PathVariable Integer id,
                                                                  @RequestBody @Valid ServicoProduto servicoProduto) {
        return ResponseEntity.ok(service.atualizar(id, servicoProduto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar serviço/produto",
            description = "Exclusão lógica: marca como inativo e mantém o histórico de vendas e dashboards")
    @ApiResponse(responseCode = "204", description = "Serviço/produto inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<Void> inativarServicoProduto(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar serviço/produto", description = "Volta um serviço/produto inativo para ativo")
    @ApiResponse(responseCode = "200", description = "Serviço/produto reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<ServicoProduto> reativarServicoProduto(@PathVariable Integer id) {
        return ResponseEntity.ok(service.reativar(id));
    }
}