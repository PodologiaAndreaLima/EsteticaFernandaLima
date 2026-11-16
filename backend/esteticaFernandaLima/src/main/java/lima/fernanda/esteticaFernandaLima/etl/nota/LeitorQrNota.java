package lima.fernanda.esteticaFernandaLima.etl.nota;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lê o QR code da NFC-e e extrai a chave de acesso de 44 dígitos. O QR contém a URL de consulta,
 * que traz a chave; isso é mais confiável que o OCR da foto. Sem QR legível, devolve vazio.
 */
public final class LeitorQrNota {
    private static final Pattern CHAVE = Pattern.compile("(?<!\\d)(\\d{44})(?!\\d)");

    private LeitorQrNota() {
    }

    public static Optional<String> lerChave(byte[] imagem) {
        if (imagem == null || imagem.length == 0) return Optional.empty();
        try {
            BufferedImage figura = ImageIO.read(new ByteArrayInputStream(imagem));
            if (figura == null) return Optional.empty();
            int largura = figura.getWidth();
            int altura = figura.getHeight();
            int[] pixels = figura.getRGB(0, 0, largura, altura, null, 0, largura);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(largura, altura, pixels)));
            Map<DecodeHintType, Object> dicas = Map.of(
                    DecodeHintType.TRY_HARDER, Boolean.TRUE,
                    DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE));
            Result resultado = new MultiFormatReader().decode(bitmap, dicas);
            Matcher casamento = CHAVE.matcher(resultado.getText());
            return casamento.find() ? Optional.of(casamento.group(1)) : Optional.empty();
        } catch (IOException | ReaderException | RuntimeException falha) {
            return Optional.empty();
        }
    }
}
