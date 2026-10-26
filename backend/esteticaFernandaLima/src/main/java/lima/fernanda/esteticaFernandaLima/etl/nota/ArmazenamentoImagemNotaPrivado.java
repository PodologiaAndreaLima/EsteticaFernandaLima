package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.config.NotaProperties;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.Set;
import java.util.Iterator;

@Component
public class ArmazenamentoImagemNotaPrivado implements ArmazenamentoImagemNota {
    private final Path diretorio;
    private final long maxBytes;
    private final long maxPixels;

    public ArmazenamentoImagemNotaPrivado(NotaProperties propriedades) {
        this.diretorio = propriedades.imagem().diretorioPrivado().normalize();
        this.maxBytes = propriedades.imagem().maxBytes();
        this.maxPixels = propriedades.imagem().maxPixels();
    }

    private void verificarDiretorio() throws IOException {
        Path atual = diretorio.getRoot();
        for (Path parte : diretorio) {
            atual = atual.resolve(parte);
            if (Files.isSymbolicLink(atual)) throw new IOException("Diretorio de imagem inseguro");
        }
        if (!Files.isDirectory(diretorio, LinkOption.NOFOLLOW_LINKS)
                || !Files.isWritable(diretorio)) throw new IOException("Diretorio de imagem indisponivel");
        try {
            var permissoes = Files.getPosixFilePermissions(diretorio, LinkOption.NOFOLLOW_LINKS);
            if (permissoes.stream().anyMatch(p -> p != PosixFilePermission.OWNER_READ
                    && p != PosixFilePermission.OWNER_WRITE && p != PosixFilePermission.OWNER_EXECUTE))
                throw new IOException("Diretorio de imagem sem isolamento");
        } catch (UnsupportedOperationException semPosix) {
            verificarAclPrivada(diretorio);
        }
    }

    private void verificarAclPrivada(Path caminho) throws IOException {
        var acl = Files.getFileAttributeView(caminho, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        var proprietario = Files.getFileAttributeView(caminho, FileOwnerAttributeView.class,
                LinkOption.NOFOLLOW_LINKS);
        if (acl == null || proprietario == null)
            throw new IOException("Volume privado sem verificacao de permissoes");
        var dono = proprietario.getOwner();
        if (acl.getAcl().stream().anyMatch(entrada -> entrada.type() == AclEntryType.ALLOW
                && !entrada.principal().equals(dono)))
            throw new IOException("Imagem privada sem isolamento de ACL");
    }

    @Override
    public void verificarDisponibilidade() throws IOException {
        verificarDiretorio();
        try (DirectoryStream<Path> raiz = Files.newDirectoryStream(diretorio.getRoot())) {
            verificarCaminhoSeguro(exigirDiretorioSeguro(raiz), diretorio.iterator());
        }
    }

    private void verificarCaminhoSeguro(SecureDirectoryStream<Path> pai, Iterator<Path> partes) throws IOException {
        if (!partes.hasNext()) {
            verificarDiretorioPrivado(pai);
            return;
        }
        try (SecureDirectoryStream<Path> filho = pai.newDirectoryStream(partes.next(), LinkOption.NOFOLLOW_LINKS)) {
            verificarCaminhoSeguro(filho, partes);
        }
    }

    private void validarChave(String chave) throws IOException {
        if (chave == null || !chave.matches("[0-9a-f]{32}\\.img")) throw new IOException("Chave de imagem invalida");
    }

    @Override
    public Imagem guardar(InputStream dados) throws IOException {
        if (dados == null) throw new IOException("Imagem ausente");
        String chave = UUID.randomUUID().toString().replace("-", "") + ".img";
        MessageDigest digest = sha256();
        try {
            verificarDiretorio();
            long total;
            try (DirectoryStream<Path> raiz = Files.newDirectoryStream(diretorio.getRoot())) {
                total = copiarNoDiretorio(exigirDiretorioSeguro(raiz), diretorio.iterator(), chave, dados, digest);
            }
            if (total == 0) throw new ImagemInvalidaException();
            byte[] bytes = ler(chave);
            byte[] calculado = digest.digest();
            if (!MessageDigest.isEqual(calculado, sha256().digest(bytes)))
                throw new IOException("Imagem privada alterada durante gravacao");
            return new Imagem(chave, HexFormat.of().formatHex(calculado), validarImagem(bytes));
        } catch (IOException | RuntimeException erro) {
            try {
                excluir(chave);
            } catch (IOException limpeza) {
                erro.addSuppressed(new IOException("Falha ao remover imagem rejeitada", limpeza));
            }
            throw erro;
        }
    }

    private long copiarNoDiretorio(SecureDirectoryStream<Path> pai, Iterator<Path> partes, String chave,
                                   InputStream dados, MessageDigest digest) throws IOException {
        if (partes.hasNext()) {
            try (SecureDirectoryStream<Path> filho = pai.newDirectoryStream(partes.next(), LinkOption.NOFOLLOW_LINKS)) {
                return copiarNoDiretorio(filho, partes, chave, dados, digest);
            }
        }
        verificarDiretorioPrivado(pai);
        Path nome = diretorio.getFileSystem().getPath(chave);
        long total = 0;
        try (SeekableByteChannel canal = pai.newByteChannel(nome,
                Set.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS),
                PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")))) {
            verificarArquivoPrivado(pai, nome);
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = dados.read(buffer)) != -1) {
                total += lidos;
                if (total > maxBytes) throw new ImagemInvalidaException();
                digest.update(buffer, 0, lidos);
                ByteBuffer bytes = ByteBuffer.wrap(buffer, 0, lidos);
                while (bytes.hasRemaining()) canal.write(bytes);
            }
        }
        return total;
    }

    private void verificarArquivoPrivado(SecureDirectoryStream<Path> pai, Path nome) throws IOException {
        var posix = pai.getFileAttributeView(nome, java.nio.file.attribute.PosixFileAttributeView.class,
                LinkOption.NOFOLLOW_LINKS);
        if (posix != null) {
            if (posix.readAttributes().permissions().stream().anyMatch(p -> p != PosixFilePermission.OWNER_READ
                    && p != PosixFilePermission.OWNER_WRITE))
                throw new IOException("Imagem privada sem isolamento");
            return;
        }
        var acl = pai.getFileAttributeView(nome, AclFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        if (acl == null) throw new IOException("Volume privado sem verificacao de permissoes");
        var dono = acl.getOwner();
        if (acl.getAcl().stream().anyMatch(entrada -> entrada.type() == AclEntryType.ALLOW
                && !entrada.principal().equals(dono)))
            throw new IOException("Imagem privada sem isolamento de ACL");
    }

    private String validarImagem(byte[] arquivo) throws IOException {
        try (ImageInputStream entrada = new MemoryCacheImageInputStream(new ByteArrayInputStream(arquivo))) {
            if (entrada == null) throw new ImagemInvalidaException();
            var leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) throw new ImagemInvalidaException();
            ImageReader leitor = leitores.next();
            try {
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                if (!formato.equals("jpeg") && !formato.equals("png") && !formato.equals("webp"))
                    throw new ImagemInvalidaException();
                try (InputStream assinatura = new ByteArrayInputStream(arquivo)) {
                    byte[] inicio = assinatura.readNBytes(12);
                    if (!assinaturaValida(inicio, formato)) throw new ImagemInvalidaException();
                }
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

    @Override
    public byte[] ler(String chave) throws IOException {
        validarChave(chave);
        try (DirectoryStream<Path> raiz = Files.newDirectoryStream(diretorio.getRoot())) {
            return lerNoDiretorio(exigirDiretorioSeguro(raiz), diretorio.iterator(), chave);
        }
    }

    private SecureDirectoryStream<Path> exigirDiretorioSeguro(DirectoryStream<Path> aberto) throws IOException {
        if (!(aberto instanceof SecureDirectoryStream<Path> seguro))
            throw new IOException("Volume privado sem abertura segura de diretorios");
        return seguro;
    }

    private byte[] lerNoDiretorio(SecureDirectoryStream<Path> pai, Iterator<Path> partes, String chave)
            throws IOException {
        if (!partes.hasNext()) {
            verificarDiretorioPrivado(pai);
            return lerArquivoPrivado(pai, chave);
        }
        try (SecureDirectoryStream<Path> filho = pai.newDirectoryStream(partes.next(), LinkOption.NOFOLLOW_LINKS)) {
            return lerNoDiretorio(filho, partes, chave);
        }
    }

    private void verificarDiretorioPrivado(SecureDirectoryStream<Path> diretorioAberto) throws IOException {
        var posix = diretorioAberto.getFileAttributeView(java.nio.file.attribute.PosixFileAttributeView.class);
        if (posix != null) {
            if (posix.readAttributes().permissions().stream().anyMatch(p -> p != PosixFilePermission.OWNER_READ
                    && p != PosixFilePermission.OWNER_WRITE && p != PosixFilePermission.OWNER_EXECUTE))
                throw new IOException("Diretorio de imagem sem isolamento");
            return;
        }
        var acl = diretorioAberto.getFileAttributeView(AclFileAttributeView.class);
        if (acl == null) throw new IOException("Volume privado sem verificacao de permissoes");
        var dono = acl.getOwner();
        if (acl.getAcl().stream().anyMatch(entrada -> entrada.type() == AclEntryType.ALLOW
                && !entrada.principal().equals(dono)))
            throw new IOException("Imagem privada sem isolamento de ACL");
    }

    private byte[] lerArquivoPrivado(SecureDirectoryStream<Path> diretorioAberto, String chave) throws IOException {
        Path nome = diretorio.getFileSystem().getPath(chave);
        var atributos = diretorioAberto.getFileAttributeView(nome, BasicFileAttributeView.class,
                LinkOption.NOFOLLOW_LINKS);
        if (atributos == null || !atributos.readAttributes().isRegularFile())
            throw new IOException("Imagem privada indisponivel");
        try (SeekableByteChannel canal = diretorioAberto.newByteChannel(nome,
                Set.of(StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS))) {
            if (canal.size() == 0 || canal.size() > maxBytes)
                throw new IOException("Imagem privada invalida");
            return lerLimitado(canal);
        }
    }

    private byte[] lerLimitado(SeekableByteChannel canal) throws IOException {
        var saida = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int lidos;
        while ((lidos = canal.read(buffer)) != -1) {
            if (lidos > maxBytes - saida.size()) throw new IOException("Imagem privada invalida");
            saida.write(buffer.array(), 0, lidos);
            buffer.clear();
        }
        if (saida.size() == 0) throw new IOException("Imagem privada invalida");
        return saida.toByteArray();
    }

    @Override
    public void excluir(String chave) throws IOException {
        validarChave(chave);
        try (DirectoryStream<Path> raiz = Files.newDirectoryStream(diretorio.getRoot())) {
            excluirNoDiretorio(exigirDiretorioSeguro(raiz), diretorio.iterator(), chave);
        }
    }

    private void excluirNoDiretorio(SecureDirectoryStream<Path> pai, Iterator<Path> partes, String chave)
            throws IOException {
        if (partes.hasNext()) {
            try (SecureDirectoryStream<Path> filho = pai.newDirectoryStream(partes.next(), LinkOption.NOFOLLOW_LINKS)) {
                excluirNoDiretorio(filho, partes, chave);
            }
            return;
        }
        verificarDiretorioPrivado(pai);
        Path nome = diretorio.getFileSystem().getPath(chave);
        var atributos = pai.getFileAttributeView(nome, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        if (atributos == null) throw new IOException("Imagem privada indisponivel");
        try {
            if (!atributos.readAttributes().isRegularFile()) throw new IOException("Imagem privada insegura");
        } catch (NoSuchFileException ausente) {
            return;
        }
        try {
            pai.deleteFile(nome);
        } catch (NoSuchFileException ausente) {
            // A exclusao ja foi concluida por outro worker.
        }
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
