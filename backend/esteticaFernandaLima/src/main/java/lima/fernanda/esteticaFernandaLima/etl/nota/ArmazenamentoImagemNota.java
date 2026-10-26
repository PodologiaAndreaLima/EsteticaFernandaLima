package lima.fernanda.esteticaFernandaLima.etl.nota;

import java.io.IOException;
import java.io.InputStream;

public interface ArmazenamentoImagemNota {
    record Imagem(String chave, String digest, String mime) { }

    Imagem guardar(InputStream dados) throws IOException;
    byte[] ler(String chave) throws IOException;
    void excluir(String chave) throws IOException;
    void verificarDisponibilidade() throws IOException;
}
