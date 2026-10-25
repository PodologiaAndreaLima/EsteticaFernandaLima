package lima.fernanda.esteticaFernandaLima.mensagem.controller;

import lima.fernanda.esteticaFernandaLima.mensagem.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MensagemExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    ResponseEntity<?> notFound(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(e.getMessage()));
    }
    @ExceptionHandler(RegraMensagemException.class)
    ResponseEntity<?> conflict(RegraMensagemException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(e.getMessage()));
    }
    @ExceptionHandler(GeracaoMensagemException.class)
    ResponseEntity<?> generation(GeracaoMensagemException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body(e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(body(message));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(body("Parâmetro inválido"));
    }
    private Map<String, Object> body(String message) {
        return Map.of("timestamp", Instant.now(), "message", message);
    }
}
