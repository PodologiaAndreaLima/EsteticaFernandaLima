package lima.fernanda.esteticaFernandaLima.integration.gemini;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;

public final class ExtracaoNotaException extends RuntimeException {
    private final CodigoFalha codigo;

    public ExtracaoNotaException(CodigoFalha codigo) {
        super(codigo.name());
        this.codigo = codigo;
    }

    public CodigoFalha codigo() { return codigo; }
}
