package lima.fernanda.esteticaFernandaLima.mensagem.controller;

import lima.fernanda.esteticaFernandaLima.mensagem.dto.*;
import lima.fernanda.esteticaFernandaLima.mensagem.service.LembreteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mensagens")
public class MensagemController {
    private final LembreteService service;
    public MensagemController(LembreteService service) { this.service = service; }

    @GetMapping
    public PageResponse<MensagemResponse> listar(@RequestParam(required = false) String clinicaId,
                                                 @RequestParam(required = false) Long lembreteId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return service.historico(clinicaId, lembreteId, page, size);
    }
}
