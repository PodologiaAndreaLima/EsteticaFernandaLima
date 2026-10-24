package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.dto.ClienteMapper;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;
import lima.fernanda.esteticaFernandaLima.enums.StatusCadastro;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Listar clientes",
            description = "Retorna clientes com filtro opcional por nome e por status (ATIVO, INATIVO ou TODOS). " +
                    "Sem o status, retorna todos (a ordem de serviço da web precisa dos inativos para exibir o histórico)")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    public ResponseEntity<List<ClienteResponse>> getCliente(@RequestParam(required = false) String busca,
                                                            @RequestParam(defaultValue = "TODOS") StatusCadastro status) {
        // Valida padrões de injection na busca
        if (busca != null && temPadraoSuspeito(busca)) {
            logger.warn("⚠️ TENTATIVA DE INJECTION no campo de busca");
            return ResponseEntity.status(400).build();
        }

        List<ClienteResponse> clientesResponse = service.buscarTodos(busca, status);
        return ResponseEntity.ok(clientesResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponse> getClientePorId(@PathVariable Integer id) {
        if (id <= 0) {
            logger.warn("⚠️ ID suspeito na busca: {}", id);
            return ResponseEntity.status(400).build();
        }

        // não encontrado → o service lança 404 com mensagem
        Cliente cliente = service.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(cliente));
    }

    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    @ApiResponse(responseCode = "409", description = "CPF ou e-mail já cadastrado")
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
    @Operation(summary = "Inativar cliente",
            description = "Exclusão lógica: marca como inativo e mantém o histórico de ordens de serviço e dashboards")
    @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Void> deleteClientePorId(@PathVariable Integer id) {
        if (id <= 0) {
            logger.warn("⚠️ ID inválido para inativação: {}", id);
            return ResponseEntity.status(400).build();
        }

        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente", description = "Volta um cliente inativo para ativo")
    @ApiResponse(responseCode = "200", description = "Cliente reativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponse> reativarCliente(@PathVariable Integer id) {
        if (id <= 0) {
            logger.warn("⚠️ ID inválido para reativação: {}", id);
            return ResponseEntity.status(400).build();
        }

        return ResponseEntity.ok(mapper.toResponse(service.reativar(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponse> putCliente(@PathVariable Integer id,
                                                      @RequestBody @Valid Cliente cliente) {
        // Valida ID e dados
        if (id <= 0) {
            logger.warn("⚠️ ID inválido para atualização: {}", id);
            return ResponseEntity.status(400).build();
        }

        if (cliente.getNomeCompleto() != null && temPadraoSuspeito(cliente.getNomeCompleto())) {
            logger.warn("⚠️ TENTATIVA DE INJECTION na atualização do nome");
            return ResponseEntity.status(400).build();
        }

        // 404 (não encontrado) e 409 (CPF/e-mail duplicado) vêm do service com mensagem
        Cliente clienteAtualizado = service.atualizar(id, cliente);
        return ResponseEntity.ok(mapper.toResponse(clienteAtualizado));
    }

    // Detecta padrões suspeitos
    private boolean temPadraoSuspeito(String entrada) {
        if (entrada == null) return false;

        String pattern = entrada.toLowerCase();

        // Detecta SQL Injection
        if (pattern.matches(".*('|(--)|(;)|(\\|\\|)|(\\*)|(\\bor\\b)|(\\band\\b)|" +
                "(\\bunion\\b)|(\\bselect\\b)|(\\binsert\\b)|(\\bupdate\\b)|(\\bdelete\\b)|(\\bdrop\\b)).*")) {
            return true;
        }

        // Detecta XSS
        return pattern.matches(".*(<script|javascript:|onerror=|onload=).*");
    }
}