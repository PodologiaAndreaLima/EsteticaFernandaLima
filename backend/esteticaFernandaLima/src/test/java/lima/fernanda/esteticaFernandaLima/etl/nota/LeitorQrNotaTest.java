package lima.fernanda.esteticaFernandaLima.etl.nota;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeitorQrNotaTest {
    private static final String CHAVE = "35261103476811001476650020000777921000376157";

    @Test
    void extraiChaveDaUrlDoQrDaNfce() throws IOException {
        String url = "https://www.nfce.fazenda.sp.gov.br/NFCeConsultaPublica/Paginas/ConsultaPublicaQRCode.aspx"
                + "?p=" + CHAVE + "|2|1|1|ABCDEF";
        byte[] png = qrComoPng(url);

        assertEquals(Optional.of(CHAVE), LeitorQrNota.lerChave(png));
    }

    @Test
    void imagemSemQrDevolveVazio() throws IOException {
        BufferedImage branca = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = branca.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, 300, 300);
        g.dispose();
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(branca, "png", saida);

        assertTrue(LeitorQrNota.lerChave(saida.toByteArray()).isEmpty());
    }

    @Test
    void bytesQueNaoSaoImagemDevolveVazio() {
        assertTrue(LeitorQrNota.lerChave("nao e imagem".getBytes()).isEmpty());
        assertTrue(LeitorQrNota.lerChave(null).isEmpty());
    }

    private static byte[] qrComoPng(String conteudo) throws IOException {
        BitMatrix matriz;
        try {
            matriz = new QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, 300, 300);
        } catch (com.google.zxing.WriterException falha) {
            throw new IOException(falha);
        }
        BufferedImage figura = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                figura.setRGB(x, y, matriz.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        ImageIO.write(figura, "png", saida);
        return saida.toByteArray();
    }
}
