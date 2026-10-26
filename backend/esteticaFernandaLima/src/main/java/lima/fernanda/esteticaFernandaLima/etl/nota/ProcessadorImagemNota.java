package lima.fernanda.esteticaFernandaLima.etl.nota;

/** The implementation must persist a terminal failure or a validated revision before returning. */
public interface ProcessadorImagemNota {
    void processar(Long notaId, Long usuarioId, byte[] imagem, String mime) throws Exception;
    boolean disponivel();
}
