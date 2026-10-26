package lima.fernanda.esteticaFernandaLima.dto.nota;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Public wire types only; no persistence or authorization decisions live here. */
public final class NotaContrato {
    private NotaContrato() {
    }

    public enum EstadoNota { PROCESSANDO, EM_REVISAO, CONFIRMADA, FALHA, DESCARTADA }
    public enum EstadoItem { PENDENTE, VALIDO, INVALIDO, CONFIRMADO, REJEITADO }
    public enum Confianca { ALTA, MEDIA, BAIXA }
    public enum CodigoFalha {
        IMAGEM_ILEGIVEL, EXTRACAO_INVALIDA, NOTA_INVALIDA,
        PROVEDOR_INDISPONIVEL, DUPLICADA, PROCESSAMENTO_FALHOU
    }
    public enum Acao { ASSOCIAR, CRIAR_NOVO, REJEITAR }
    public enum CodigoErro {
        SOLICITACAO_INVALIDA, IMAGEM_INVALIDA, DUPLICADA, NOTA_INVALIDA,
        CONFLITO_ESTADO, SEM_ACESSO, LIMITE_EXCEDIDO, SERVICO_INDISPONIVEL
    }
    public enum MotivoRejeicao { SEM_PRODUTO, REJEITADO_PELA_USUARIA, DADOS_INVALIDOS }

    public record UploadResposta(@NotNull @Positive Long notaId, @NotNull EstadoNota status) {
    }

    public record Fornecedor(String cnpj, String razaoSocial) {
    }

    public record ProdutoSugerido(@NotNull @Positive Long id, @NotBlank String nome) {
    }

    public record Rejeicao(@NotNull MotivoRejeicao codigo, @NotBlank @Size(max = 160) String texto) {
    }

    public record Item(@NotNull @Positive Long itemId, @NotBlank String textoOriginal,
                       Integer quantidade, BigDecimal valorUnitario, BigDecimal valorTotal,
                       ProdutoSugerido produtoSugerido, Confianca confianca,
                       @NotNull EstadoItem status, Rejeicao motivoRejeicao) {
    }

    public record NotaResposta(@NotNull @Positive Long notaId, @NotNull EstadoNota status,
                               Fornecedor fornecedor, LocalDate dataEmissao, BigDecimal totalNota,
                               String chaveAcesso, @NotNull List<String> avisos,
                               CodigoFalha codigoFalha, @NotNull List<Item> itens,
                               ResumoConfirmacao resumoConfirmacao) {
    }

    public record ConfirmarRequest(@NotEmpty List<@NotNull @Valid DecisaoItem> itens) {
    }

    public record DecisaoItem(@NotNull @Positive Long itemId, @NotNull Acao acao,
                              @Positive Long produtoId, @NotNull Boolean criarNovo,
                              String nomeNovo, @Positive Integer quantidade,
                              @DecimalMin("0.01") @Digits(integer = 7, fraction = 2) BigDecimal valorUnitario) {
    }

    public record ItemConfirmado(@NotNull @Positive Long itemId, @NotNull @Positive Long produtoId,
                                 @NotNull @Positive Integer quantidade, @NotNull BigDecimal custoUnitario) {
    }

    public record ItemRejeitado(@NotNull @Positive Long itemId, @NotNull MotivoRejeicao motivo) {
    }

    public record ProdutoAfetado(@NotNull @Positive Long id, @NotNull Integer estoqueAtual,
                                 @NotNull BigDecimal ultimoCusto, BigDecimal valorVenda) {
    }

    public record ResumoConfirmacao(@NotNull @Positive Long notaId, @NotNull EstadoNota status,
                                    @NotNull List<ItemConfirmado> itensConfirmados,
                                    @NotNull List<ItemRejeitado> itensRejeitados,
                                    @NotNull List<ProdutoAfetado> produtosAfetados) {
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record Erro(@NotNull CodigoErro codigo, @NotBlank String mensagem,
                       Long notaId, EstadoNota statusAtual) {
    }

    public record ProdutoCatalogo(@NotNull @Positive Long id, @NotBlank String nome) {
    }
}
