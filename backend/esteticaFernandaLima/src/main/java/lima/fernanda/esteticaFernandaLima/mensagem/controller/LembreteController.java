package lima.fernanda.esteticaFernandaLima.mensagem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lima.fernanda.esteticaFernandaLima.mensagem.dto.*;
import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;
import lima.fernanda.esteticaFernandaLima.mensagem.service.LembreteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lembretes")
@Tag(name = "Lembretes", description = "Criação, cancelamento e envio de lembretes")
public class LembreteController {
    private final LembreteService service;
    public LembreteController(LembreteService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Criar lembrete")
    public ResponseEntity<LembreteResponse> criar(@Valid @RequestBody CriarLembreteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar lembretes com paginação")
    public PageResponse<LembreteResponse> listar(
            @RequestParam(required = false) String clinicaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listar(clinicaId, page, size);
    }

    @GetMapping("/{id}")
    public LembreteResponse buscar(@PathVariable Long id, @RequestParam(required = false) String clinicaId) {
        return service.mapperResponse(service.buscar(id, clinicaId));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar lembrete")
    public LembreteResponse cancelar(@PathVariable Long id, @RequestParam(required = false) String clinicaId) {
        return service.cancelar(id, clinicaId);
    }

    @PostMapping("/{id}/enviar")
    @Operation(summary = "Enviar mensagem do lembrete")
    public MensagemResponse enviar(@PathVariable Long id, @RequestParam(required = false) String clinicaId,
                                   @RequestParam(defaultValue = "WHATSAPP") CanalMensagem canal,
                                   @Valid @RequestBody(required = false) EnviarMensagemRequest request) {
        return service.enviar(id, clinicaId,
                request == null ? new EnviarMensagemRequest(canal, null)
                        : new EnviarMensagemRequest(request.canal() == null ? canal : request.canal(), request.conteudo()));
    }

    @PostMapping("/{id}/gerar")
    @Operation(summary = "Gerar mensagem para revisão humana, sem enviar")
    public GerarMensagemResponse gerar(@PathVariable Long id,
                                       @RequestParam(required = false) String clinicaId) {
        return service.gerar(id, clinicaId);
    }

    @GetMapping("/historico/mensagens")
    @Operation(summary = "Consultar histórico de mensagens com paginação")
    public PageResponse<MensagemResponse> historico(@RequestParam(required = false) String clinicaId,
                                                    @RequestParam(required = false) Long lembreteId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return service.historico(clinicaId, lembreteId, page, size);
    }
}
