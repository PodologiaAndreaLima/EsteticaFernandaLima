package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.dto.ClienteMapper;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
@SecurityRequirement(name = "Bearer")
public class ClienteController {

    private final ClienteService service;
    private final ClienteMapper mapper; // ✅ ADICIONAR

    public ClienteController(ClienteService service, ClienteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
@Operation(summary = "Listar clientes", description = "Retorna todos os clientes ou filtra por nome")
@ApiResponse(responseCode = "200", description = "Clientes encontrados")
@ApiResponse(responseCode = "204", description = "Nenhum cliente encontrado")
public ResponseEntity<List<ClienteResponse>> getCliente(@RequestParam(required = false) String busca) {
    List<ClienteResponse> clientesResponse = service.buscarTodos(busca);
    return clientesResponse.isEmpty() ?
            ResponseEntity.noContent().build() :
            ResponseEntity.ok(clientesResponse);
}

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponse> getClientePorId(@PathVariable Integer id) { 
        try {
            Cliente cliente = service.buscarPorId(id);
            return ResponseEntity.ok(mapper.toResponse(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    public ResponseEntity<ClienteResponse> postCliente(@RequestBody @Valid Cliente cliente) { // ✅ MUDAR para ClienteResponse
        Cliente clienteSalvo = service.salvar(cliente);
        return ResponseEntity.status(201).body(mapper.toResponse(clienteSalvo)); // ✅ MAPEAR
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo ID")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Void> deleteClientePorId(@PathVariable Integer id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponse> putCliente(@PathVariable Integer id, // ✅ MUDAR para ClienteResponse
                                                      @RequestBody @Valid Cliente cliente) {
        try {
            Cliente clienteAtualizado = service.atualizar(id, cliente);
            return ResponseEntity.ok(mapper.toResponse(clienteAtualizado)); // ✅ MAPEAR
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}