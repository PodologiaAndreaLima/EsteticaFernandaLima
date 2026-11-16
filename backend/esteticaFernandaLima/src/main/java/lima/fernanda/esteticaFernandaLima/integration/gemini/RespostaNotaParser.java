package lima.fernanda.esteticaFernandaLima.integration.gemini;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RespostaNotaParser {
    private static final Logger LOG = LoggerFactory.getLogger(RespostaNotaParser.class);
    private static final ObjectMapper JSON = new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

    public ExtracaoNota interpretar(String texto) {
        if (texto == null || texto.length() > 24_000) throw invalida("resposta ausente ou grande demais: " + (texto == null ? "null" : texto.length() + " caracteres"));
        try {
            JsonNode raiz = JSON.readTree(texto);
            campos(raiz, Set.of("legivel", "motivo", "fornecedor", "dataEmissao", "totalNota", "chaveAcesso", "itens"), "raiz");
            if (!raiz.get("legivel").isBoolean()) throw invalida("campo legivel nao e booleano");
            JsonNode fornecedor = raiz.get("fornecedor");
            ExtracaoNota.Fornecedor dados = null;
            if (!fornecedor.isNull()) {
                campos(fornecedor, Set.of("cnpj", "razaoSocial"), "fornecedor");
                dados = new ExtracaoNota.Fornecedor(campo(fornecedor, "cnpj"), campo(fornecedor, "razaoSocial"));
            }
            JsonNode itens = raiz.get("itens");
            if (!itens.isArray() || itens.size() > 100) throw invalida("itens invalidos: " + (itens.isArray() ? itens.size() + " itens" : "nao e lista"));
            List<ExtracaoNota.Item> linhas = new ArrayList<>();
            for (JsonNode item : itens) {
                campos(item, Set.of("texto", "quantidade", "valorUnitario", "valorTotal"), "item");
                linhas.add(new ExtracaoNota.Item(campo(item, "texto"), campo(item, "quantidade"),
                        campo(item, "valorUnitario"), campo(item, "valorTotal")));
            }
            return new ExtracaoNota(raiz.get("legivel").booleanValue(), campo(raiz, "motivo"), dados,
                    campo(raiz, "dataEmissao"), campo(raiz, "totalNota"), campo(raiz, "chaveAcesso"), List.copyOf(linhas));
        } catch (com.fasterxml.jackson.core.JsonProcessingException falha) {
            throw invalida("JSON invalido (" + falha.getClass().getSimpleName() + ")");
        } catch (IllegalArgumentException falha) {
            throw invalida("campo fora do formato esperado");
        }
    }

    private static void campos(JsonNode node, Set<String> esperados, String onde) {
        if (node == null || !node.isObject()) throw invalida(onde + " nao e objeto");
        if (node.size() != esperados.size()) throw invalida(onde + " com " + node.size() + " campos (esperado " + esperados.size() + ")");
        for (String nome : esperados) if (!node.has(nome)) throw invalida(onde + " sem o campo " + nome);
    }

    private static String campo(JsonNode node, String nome) {
        JsonNode valor = node.get(nome);
        if (valor.isNull()) return null;
        if (!valor.isTextual()) throw invalida("campo " + nome + " nao e texto");
        if (valor.textValue().length() > 500) throw invalida("campo " + nome + " longo demais (" + valor.textValue().length() + ")");
        return valor.textValue();
    }

    private static ExtracaoNotaException invalida(String motivo) {
        // Diagnostico: so o motivo tecnico, nunca o conteudo da nota.
        LOG.warn("Resposta do Gemini recusada: {}", motivo);
        return new ExtracaoNotaException(CodigoFalha.EXTRACAO_INVALIDA);
    }
}
