package lima.fernanda.esteticaFernandaLima.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lima.fernanda.esteticaFernandaLima.dto.UsuarioDetalhesDto;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.*;
import lima.fernanda.esteticaFernandaLima.repository.ServicoProdutoRepository;
import lima.fernanda.esteticaFernandaLima.etl.nota.SubmissaoNotaService;
import lima.fernanda.esteticaFernandaLima.service.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/estoque/notas")
public class NotaFiscalController {
    private final NotaFiscalRevisaoService revisao;
    private final NotaFiscalConfirmacaoService confirmacao;
    private final ServicoProdutoRepository produtos;
    private final SubmissaoNotaService submissao;
    private final ObjectMapper json;
    private final Validator validador;

    public NotaFiscalController(NotaFiscalRevisaoService revisao, NotaFiscalConfirmacaoService confirmacao,
                                 ServicoProdutoRepository produtos, SubmissaoNotaService submissao,
                                 ObjectMapper json, Validator validador) {
        this.revisao = revisao;
        this.confirmacao = confirmacao;
        this.produtos = produtos;
        this.submissao = submissao;
        this.json = json;
        this.validador = validador;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResposta> subir(MultipartHttpServletRequest request,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem,
            @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        Long usuarioId = dono(usuario);
        if (request.getMultiFileMap().size() != 1 || request.getFiles("imagem").size() != 1
                || !request.getParameterMap().isEmpty() || imagem == null || imagem.isEmpty()) {
            throw new NotaFiscalErro(HttpStatus.BAD_REQUEST, CodigoErro.SOLICITACAO_INVALIDA, null);
        }
        UploadResposta resposta = submissao.submeter(usuarioId, imagem);
        return ResponseEntity.accepted().header("Location", "/estoque/notas/" + resposta.notaId())
                .header("Cache-Control", "no-store").body(resposta);
    }

    @GetMapping("/produtos")
    public List<ProdutoCatalogo> produtos(@AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        return produtos.findByUsuarioIdAndProdutoTrueAndAtivoTrueOrderByIdProdutoServicoAsc(dono(usuario))
                .stream().map(p -> new ProdutoCatalogo(p.getIdProdutoServico().longValue(), p.getNome())).toList();
    }

    @GetMapping("/{id}")
    public NotaResposta consultar(@PathVariable Long id, @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        return revisao.consultar(idNota(id), dono(usuario));
    }

    @PostMapping("/{id}/descartar")
    public ResponseEntity<Void> descartar(@PathVariable Long id, @AuthenticationPrincipal UsuarioDetalhesDto usuario) {
        revisao.descartar(idNota(id), dono(usuario));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirmar")
    public ResumoConfirmacao confirmar(@PathVariable Long id, @AuthenticationPrincipal UsuarioDetalhesDto usuario,
                                       @RequestBody String corpo) {
        ConfirmarRequest request;
        try {
            request = json.readerFor(ConfirmarRequest.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(corpo);
        } catch (JsonProcessingException falha) {
            throw new NotaFiscalErro(HttpStatus.BAD_REQUEST, CodigoErro.SOLICITACAO_INVALIDA, null);
        }
        if (request == null || !validador.validate(request).isEmpty()) {
            throw new NotaFiscalErro(HttpStatus.BAD_REQUEST, CodigoErro.SOLICITACAO_INVALIDA, null);
        }
        return confirmacao.confirmar(idNota(id), dono(usuario), request);
    }

    private Long idNota(Long id) {
        if (id == null || id <= 0) throw new NotaFiscalErro(HttpStatus.BAD_REQUEST, CodigoErro.SOLICITACAO_INVALIDA, null);
        return id;
    }

    private Long dono(UsuarioDetalhesDto usuario) {
        if (usuario == null || usuario.getId() == null || usuario.getId() <= 0) {
            throw new NotaFiscalErro(HttpStatus.UNAUTHORIZED, CodigoErro.SEM_ACESSO, null);
        }
        return usuario.getId();
    }
}
