package lima.fernanda.esteticaFernandaLima.controller;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.*;
import lima.fernanda.esteticaFernandaLima.service.NotaFiscalErro;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice(assignableTypes = NotaFiscalController.class)
public class NotaFiscalErrosController {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Erro> grande(MaxUploadSizeExceededException erro) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new Erro(CodigoErro.IMAGEM_INVALIDA,
                mensagem(CodigoErro.IMAGEM_INVALIDA), null, null));
    }

    @ExceptionHandler(NotaFiscalErro.class)
    public ResponseEntity<Erro> conhecida(NotaFiscalErro erro) {
        return ResponseEntity.status(erro.getHttpStatus()).body(new Erro(erro.getCodigo(),
                mensagem(erro.getCodigo()), erro.getNotaId(), erro.getStatusAtual()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<Erro> invalida(Exception erro) {
        return ResponseEntity.badRequest().body(new Erro(CodigoErro.SOLICITACAO_INVALIDA,
                mensagem(CodigoErro.SOLICITACAO_INVALIDA), null, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Erro> inesperada(Exception erro) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Erro(CodigoErro.SERVICO_INDISPONIVEL,
                mensagem(CodigoErro.SERVICO_INDISPONIVEL), null, null));
    }

    private String mensagem(CodigoErro codigo) {
        return switch (codigo) {
            case CONFLITO_ESTADO -> "O estado da nota mudou. Consulte novamente.";
            case NOTA_INVALIDA -> "Nota não encontrada.";
            case SEM_ACESSO -> "Acesso não autorizado.";
            case LIMITE_EXCEDIDO -> "Limite de solicitações excedido.";
            case SERVICO_INDISPONIVEL -> "Serviço indisponível.";
            default -> "Confira os dados enviados.";
        };
    }
}
