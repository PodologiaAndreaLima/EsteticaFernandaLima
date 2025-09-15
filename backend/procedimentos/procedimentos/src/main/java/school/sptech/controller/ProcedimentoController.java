package school.sptech.controller;

import school.sptech.model.Procedimento;
import school.sptech.service.ProcedimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/procedimentos")
public class ProcedimentoController {

    private final ProcedimentoService service;

    public ProcedimentoController(ProcedimentoService service) {

        this.service = service;
    }

    @GetMapping
    public List<Procedimento> listarTodos() {

        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Procedimento> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Procedimento criar(@RequestBody Procedimento procedimento) {

        return service.salvar(procedimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Procedimento> atualizar(@PathVariable Long id, @RequestBody Procedimento procedimento) {
        return service.buscarPorId(id)
                .map(p -> {
                    p.setNome(procedimento.getNome());
                    p.setDescricao(procedimento.getDescricao());
                    p.setValor(procedimento.getValor());
                    p.setTempo(procedimento.getTempo());
                    return ResponseEntity.ok(service.salvar(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}