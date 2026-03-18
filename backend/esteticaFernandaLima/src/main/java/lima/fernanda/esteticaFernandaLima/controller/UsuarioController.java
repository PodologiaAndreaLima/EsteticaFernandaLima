package lima.fernanda.esteticaFernandaLima.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioListarDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioMapper;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioLoginDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioTokenDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioCriacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioAtualizacaoDto;
import lima.fernanda.esteticaFernandaLima.dto.TrocaSenhaDto;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de usuários da aplicação")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> criar(@RequestBody @Valid UsuarioCriacaoDto usuarioCriacaoDto) {
        final Usuario novoUsuario = UsuarioMapper.of(usuarioCriacaoDto);
        this.usuarioService.criar(novoUsuario);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioTokenDto> login(@RequestBody UsuarioLoginDto usuarioLoginDto) {

        final Usuario usuario = UsuarioMapper.of(usuarioLoginDto);
        UsuarioTokenDto usuarioTokenDto = this.usuarioService.autenticar(usuario);

        return ResponseEntity.status(200).body(usuarioTokenDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<UsuarioListarDto>> listarTodos() {

        List<UsuarioListarDto> usuariosEncontrados = this.usuarioService.listarTodos();

        if (usuariosEncontrados.isEmpty()){
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.status(200).body(usuariosEncontrados);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "Bearer")
public ResponseEntity<UsuarioListarDto> atualizar(
    @PathVariable Long id,
    @RequestBody @Valid UsuarioAtualizacaoDto usuarioAtualizacaoDto) {
    try {
        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuarioAtualizacaoDto);
        logger.info("Usuário atualizado: {}", id);
        return ResponseEntity.ok(UsuarioMapper.of(usuarioAtualizado));
    } catch (org.springframework.web.server.ResponseStatusException e) {
        throw e;
    } catch (Exception e) {
        logger.error("Erro ao atualizar usuário: {}", e.getMessage());
        return ResponseEntity.status(400).build();
    }
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer")
public ResponseEntity<Void> deletar(@PathVariable Long id) {
    try {
        usuarioService.deletar(id);
        logger.info("Usuário deletado com sucesso: {}", id);
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        logger.error("Erro ao deletar usuário: {}", e.getMessage());
        return ResponseEntity.status(400).build();
    }
}

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<UsuarioListarDto> buscarPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(UsuarioMapper.of(usuario));
        } catch (RuntimeException e) {
            logger.warn("Usuário não encontrado: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/alterar-senha")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Long id,
            @RequestBody @Valid TrocaSenhaDto dto
    ) {
        usuarioService.alterarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}
