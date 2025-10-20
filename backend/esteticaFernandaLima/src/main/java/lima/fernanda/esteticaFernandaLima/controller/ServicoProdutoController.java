package lima.fernanda.esteticaFernandaLima.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;
import lima.fernanda.esteticaFernandaLima.service.ServicoProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico-produto")
public class ServicoProdutoController {

    private final ServicoProdutoService service;

    public ServicoProdutoController(ServicoProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar serviços/produtos", description = "Retorna todos os serviços/produtos ou filtra por nome")
    @ApiResponse(responseCode = "200", description = "Serviços/produtos encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum serviço/produto encontrado")
        public ResponseEntity<List<ServicoProduto>> getServicoProduto() {
        java.util.List<ServicoProduto> servicoProdutos = service.buscarTodos();
        return servicoProdutos.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(servicoProdutos);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço/produto por ID", description = "Retorna um serviço/produto específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Serviço/produto encontrado")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<ServicoProduto> getServicoProdutoPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto ou serviço", description = "Cadastra um novo produto ou serviço")
    @ApiResponse(responseCode = "201", description = "Produto/serviço cadastrado com sucesso")
    public ResponseEntity<ServicoProduto> postServicoProduto(@RequestBody @Valid ServicoProduto servicoProduto) {
        return ResponseEntity.status(201).body(service.salvar(servicoProduto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Serviço/produto", description = "Remove um Serviço/produto pelo ID")
    @ApiResponse(responseCode = "204", description = "Serviço/produto removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<Void> deleteServiçoProdutoPorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço/produto", description = "Atualiza um serviço/produto pelo ID")
    @ApiResponse(responseCode = "200", description = "Serviço/produto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Serviço/produto não encontrado")
    public ResponseEntity<ServicoProduto> atualizarServicoProduto(@PathVariable Integer id, @RequestBody @Valid ServicoProduto servicoProduto) {
        try {
            ServicoProduto atualizado = service.atualizar(id, servicoProduto);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
