package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.config.NotaProperties;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A foto da nota fica somente em memoria, nunca em disco nem no banco. O worker a remove assim que
 * a extracao termina; se o processo reiniciar antes disso, a nota falha e a usuaria envia a foto de novo.
 */
@Component
public class ArmazenamentoImagemNotaMemoria implements ArmazenamentoImagemNota {
    private final Map<String, byte[]> imagens = new ConcurrentHashMap<>();
    private final long maxBytes;
    private final long maxPixels;

    public ArmazenamentoImagemNotaMemoria(NotaProperties propriedades) {
        this.maxBytes = propriedades.imagem().maxBytes();
        this.maxPixels = propriedades.imagem().maxPixels();
    }

    @Override
    public Imagem guardar(InputStream dados) throws IOException {
        if (dados == null) throw new IOException("Imagem ausente");
        byte[] bytes = dados.readNBytes((int) maxBytes + 1);
        if (bytes.length == 0 || bytes.length > maxBytes) throw new ImagemInvalidaException();
        String mime = validarImagem(bytes);
        String chave = UUID.randomUUID().toString().replace("-", "");
        imagens.put(chave, bytes);
        return new Imagem(chave, HexFormat.of().formatHex(sha256().digest(bytes)), mime);
    }

    @Override
    public byte[] ler(String chave) throws IOException {
        byte[] bytes = chave == null ? null : imagens.get(chave);
        if (bytes == null) throw new ImagemIndisponivelException();
        return bytes;
    }

    @Override
    public void excluir(String chave) {
        // Idempotente: excluir uma imagem ja removida nao e erro.
        if (chave != null) imagens.remove(chave);
    }

    private String validarImagem(byte[] arquivo) throws IOException {
        try (ImageInputStream entrada = new MemoryCacheImageInputStream(new ByteArrayInputStream(arquivo))) {
            var leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) throw new ImagemInvalidaException();
            ImageReader leitor = leitores.next();
            try {
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                if (!formato.equals("jpeg") && !formato.equals("png") && !formato.equals("webp"))
                    throw new ImagemInvalidaException();
                if (!assinaturaValida(arquivo, formato)) throw new ImagemInvalidaException();
                try {
                    leitor.setInput(entrada, true, true);
                    long largura = leitor.getWidth(0);
                    long altura = leitor.getHeight(0);
                    if (largura <= 0 || altura <= 0 || largura > maxPixels / altura || leitor.read(0) == null)
                        throw new ImagemInvalidaException();
                } catch (IOException decodificacao) {
                    throw new ImagemInvalidaException();
                }
                return formato.equals("jpeg") ? "image/jpeg" : "image/" + formato;
            } finally {
                leitor.dispose();
            }
        } catch (RuntimeException erro) {
            throw new ImagemInvalidaException();
        }
    }

    private boolean assinaturaValida(byte[] b, String formato) {
        return switch (formato) {
            case "jpeg" -> b.length >= 3 && (b[0] & 255) == 255 && (b[1] & 255) == 216 && (b[2] & 255) == 255;
            case "png" -> b.length >= 8 && b[0] == (byte) 137 && b[1] == 80 && b[2] == 78
                    && b[3] == 71 && b[4] == 13 && b[5] == 10 && b[6] == 26 && b[7] == 10;
            case "webp" -> b.length >= 12 && b[0] == 82 && b[1] == 73 && b[2] == 70
                    && b[3] == 70 && b[8] == 87 && b[9] == 69 && b[10] == 66 && b[11] == 80;
            default -> false;
        };
    }

    private MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException erro) {
            throw new IllegalStateException("SHA-256 indisponivel", erro);
        }
    }

    public static class ImagemInvalidaException extends IOException {
        public ImagemInvalidaException() { super("Imagem invalida"); }
    }
}
