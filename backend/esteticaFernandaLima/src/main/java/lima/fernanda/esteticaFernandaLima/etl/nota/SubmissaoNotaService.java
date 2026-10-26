package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.*;
import lima.fernanda.esteticaFernandaLima.model.NotaFiscalIdentidade.Tipo;
import lima.fernanda.esteticaFernandaLima.repository.NotaFiscalIdentidadeRepository;
import lima.fernanda.esteticaFernandaLima.service.NotaFiscalErro;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SubmissaoNotaService {
    private final ArmazenamentoImagemNota armazenamento;
    private final ReservaNota reserva;
    private final NotaFiscalIdentidadeRepository identidades;
    private final ObjectProvider<ProcessadorImagemNota> processador;

    public SubmissaoNotaService(ArmazenamentoImagemNota armazenamento, ReservaNota reserva,
                                NotaFiscalIdentidadeRepository identidades,
                                ObjectProvider<ProcessadorImagemNota> processador) {
        this.armazenamento = armazenamento;
        this.reserva = reserva;
        this.identidades = identidades;
        this.processador = processador;
    }

    public UploadResposta submeter(Long usuarioId, MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) throw erro(HttpStatus.BAD_REQUEST, CodigoErro.IMAGEM_INVALIDA);
        var cliente = processador.getIfAvailable();
        if (cliente == null || !cliente.disponivel()) throw erro(HttpStatus.SERVICE_UNAVAILABLE, CodigoErro.SERVICO_INDISPONIVEL);
        ArmazenamentoImagemNota.Imagem imagem;
        try {
            armazenamento.verificarDisponibilidade();
            try (var entrada = arquivo.getInputStream()) {
                imagem = armazenamento.guardar(entrada);
            }
        } catch (ArmazenamentoImagemNotaPrivado.ImagemInvalidaException invalida) {
            throw erro(HttpStatus.BAD_REQUEST, CodigoErro.IMAGEM_INVALIDA);
        } catch (IOException falha) {
            throw erro(HttpStatus.SERVICE_UNAVAILABLE, CodigoErro.SERVICO_INDISPONIVEL);
        }
        try {
            return new UploadResposta(reserva.reservar(usuarioId, imagem), EstadoNota.PROCESSANDO);
        } catch (RuntimeException falha) {
            try {
                armazenamento.excluir(imagem.chave());
            } catch (IOException limpeza) {
                falha.addSuppressed(new IOException("Falha na compensacao da imagem", limpeza));
            }
            if (falha instanceof DataIntegrityViolationException) {
                var propria = identidades.findByUsuarioIdAndTipoIdentidadeAndIdentidadeDigest(
                        usuarioId, Tipo.SOMENTE_IMAGEM, imagem.digest());
                if (propria.isPresent()) throw new NotaFiscalErro(HttpStatus.CONFLICT, CodigoErro.DUPLICADA,
                        null, propria.get().getNota().getId());
            }
            throw erro(HttpStatus.SERVICE_UNAVAILABLE, CodigoErro.SERVICO_INDISPONIVEL);
        }
    }

    private NotaFiscalErro erro(HttpStatus status, CodigoErro codigo) {
        return new NotaFiscalErro(status, codigo, null);
    }
}
