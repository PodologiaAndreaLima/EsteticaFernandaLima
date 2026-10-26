package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoErro;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.EstadoNota;
import org.springframework.http.HttpStatus;

public class NotaFiscalErro extends RuntimeException {
    private final HttpStatus httpStatus;
    private final CodigoErro codigo;
    private final EstadoNota statusAtual;
    private final Long notaId;

    public NotaFiscalErro(HttpStatus httpStatus, CodigoErro codigo, EstadoNota statusAtual) {
        this(httpStatus, codigo, statusAtual, null);
    }

    public NotaFiscalErro(HttpStatus httpStatus, CodigoErro codigo, EstadoNota statusAtual, Long notaId) {
        super(codigo.name());
        this.httpStatus = httpStatus;
        this.codigo = codigo;
        this.statusAtual = statusAtual;
        this.notaId = notaId;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
    public CodigoErro getCodigo() { return codigo; }
    public EstadoNota getStatusAtual() { return statusAtual; }
    public Long getNotaId() { return notaId; }
}
