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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
@SecurityRequirement(name = "Bearer")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService service;
    private final ClienteMapper mapper;

    public ClienteController(ClienteService service, ClienteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes ou filtra por nome")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @ApiResponse(responseCode = "204", description = "Nenhum cliente encontrado")
    public ResponseEntity<List<ClienteResponse>> getCliente(@RequestParam(required = false) String busca) {
        // Valida padrões de injection na busca
        if (busca != null && temPadraoSuspeito(busca)) {
            logger.warn("⚠️ TENTATIVA DE INJECTION no campo de busca");
            return ResponseEntity.status(400).build();
        }

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
            if (id <= 0) {
                logger.warn("⚠️ ID suspeito na busca: {}", id);
                return ResponseEntity.status(400).build();
            }

            Cliente cliente = service.buscarPorId(id);
            return ResponseEntity.ok(mapper.toResponse(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    public ResponseEntity<ClienteResponse> postCliente(@RequestBody @Valid Cliente cliente) {
        //Valida dados do cliente
        if (cliente.getNomeCompleto() != null && temPadraoSuspeito(cliente.getNomeCompleto())) {
            logger.warn("⚠️ TENTATIVA DE INJECTION no nome do cliente");
            return ResponseEntity.status(400).build();
        }

        if (cliente.getEmail() != null && temPadraoSuspeito(cliente.getEmail())) {
            logger.warn("⚠️ TENTATIVA DE INJECTION no email do cliente");
            return ResponseEntity.status(400).build();
        }

        Cliente clienteSalvo = service.salvar(cliente);
        return ResponseEntity.status(201).body(mapper.toResponse(clienteSalvo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo ID")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Void> deleteClientePorId(@PathVariable Integer id) {
        try {
            if (id <= 0) {
                logger.warn("⚠️ ID inválido para deleção: {}", id);
                return ResponseEntity.status(400).build();
            }

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
    public ResponseEntity<ClienteResponse> putCliente(@PathVariable Integer id,
                                                      @RequestBody @Valid Cliente cliente) {
        try {
            // Valida ID e dados
            if (id <= 0) {
                logger.warn("⚠️ ID inválido para atualização: {}", id);
                return ResponseEntity.status(400).build();
            }

            if (cliente.getNomeCompleto() != null && temPadraoSuspeito(cliente.getNomeCompleto())) {
                logger.warn("⚠️ TENTATIVA DE INJECTION na atualização do nome");
                return ResponseEntity.status(400).build();
            }

            Cliente clienteAtualizado = service.atualizar(id, cliente);
            return ResponseEntity.ok(mapper.toResponse(clienteAtualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Detecta padrões suspeitos
    private boolean temPadraoSuspeito(String entrada) {
        if (entrada == null) return false;

        String pattern = entrada.toLowerCase();

        // Detecta SQL Injection
        if (pattern.matches(".*('|(\\-\\-)|(;)|(\\|\\|)|(\\*)|(\\bor\\b)|(\\band\\b)|" +
                "(\\bunion\\b)|(\\bselect\\b)|(\\binsert\\b)|(\\bupdate\\b)|(\\bdelete\\b)|(\\bdrop\\b)).*")) {
            return true;
        }

        // Detecta XSS
        if (pattern.matches(".*(<script|javascript:|onerror=|onload=).*")) {
            return true;
        }

        return false;
    }
}