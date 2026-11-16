package lima.fernanda.esteticaFernandaLima.etl.nota;

import java.io.IOException;
import java.io.InputStream;

public interface ArmazenamentoImagemNota {
    record Imagem(String chave, String digest, String mime) { }

    Imagem guardar(InputStream dados) throws IOException;
    byte[] ler(String chave) throws IOException;
    void excluir(String chave) throws IOException;

    /** Imagem ausente: expirou ou o processo reiniciou antes da extracao terminar. */
    class ImagemIndisponivelException extends IOException {
        public ImagemIndisponivelException() { super("Imagem indisponivel para processamento"); }
    }
}
