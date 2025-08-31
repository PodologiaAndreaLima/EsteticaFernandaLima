package school.sptech.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.model.Cliente;
import school.sptech.repository.ClienteRepository;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository){this.repository = repository;}

    // Buscar todos os clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> getCliente(){
        var cliente = repository.findAll();
        return cliente.isEmpty()?ResponseEntity.status(204).build():ResponseEntity.status(200).body(cliente);
    }

    // Buscando cliente filtrado pelo nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> getByNome(@RequestParam String nome) {
        var cliente = repository.findByNomeCompletoContainingIgnoreCase(nome);
        return cliente.isEmpty()
                ? ResponseEntity.status(204).build()
                : ResponseEntity.status(200).body(cliente);
    }

    // Buscando cliente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getPetPorId(@PathVariable Integer id) {
        if (repository.existsById(id)) {
            Cliente clienteEncontrado = repository.findById(id).get();
            return ResponseEntity.ok(clienteEncontrado);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    // Cadastrando Cliente
    @PostMapping
    public ResponseEntity<Cliente> postPet(@RequestBody @Valid Cliente cliente) {
        return ResponseEntity.status(201).body(repository.save(cliente));
    }

    // Deletando Cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePetPorId(@PathVariable Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    // Editando Cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Integer id,
                                             @RequestBody @Valid Cliente clienteAtualizado) {
        return repository.findById(id)
                .map(cliente -> {
                    cliente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
                    cliente.setCpf(clienteAtualizado.getCpf());
                    cliente.setTelefone(clienteAtualizado.getTelefone());
                    cliente.setEmail(clienteAtualizado.getEmail());
                    cliente.setSenha(clienteAtualizado.getSenha());

                    Cliente salvo = repository.save(cliente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
