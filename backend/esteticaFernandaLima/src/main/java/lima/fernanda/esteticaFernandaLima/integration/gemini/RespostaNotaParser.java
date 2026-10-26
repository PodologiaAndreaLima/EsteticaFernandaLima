package lima.fernanda.esteticaFernandaLima.integration.gemini;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class RespostaNotaParser {
    private static final ObjectMapper JSON = new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

    public ExtracaoNota interpretar(String texto) {
        if (texto == null || texto.length() > 24_000) throw invalida();
        try {
            JsonNode raiz = JSON.readTree(texto);
            campos(raiz, Set.of("legivel", "motivo", "fornecedor", "dataEmissao", "totalNota", "chaveAcesso", "itens"));
            if (!raiz.get("legivel").isBoolean()) throw invalida();
            JsonNode fornecedor = raiz.get("fornecedor");
            ExtracaoNota.Fornecedor dados = null;
            if (!fornecedor.isNull()) {
                campos(fornecedor, Set.of("cnpj", "razaoSocial"));
                dados = new ExtracaoNota.Fornecedor(campo(fornecedor, "cnpj"), campo(fornecedor, "razaoSocial"));
            }
            JsonNode itens = raiz.get("itens");
            if (!itens.isArray() || itens.size() > 100) throw invalida();
            List<ExtracaoNota.Item> linhas = new ArrayList<>();
            for (JsonNode item : itens) {
                campos(item, Set.of("texto", "quantidade", "valorUnitario", "valorTotal"));
                linhas.add(new ExtracaoNota.Item(campo(item, "texto"), campo(item, "quantidade"),
                        campo(item, "valorUnitario"), campo(item, "valorTotal")));
            }
            return new ExtracaoNota(raiz.get("legivel").booleanValue(), campo(raiz, "motivo"), dados,
                    campo(raiz, "dataEmissao"), campo(raiz, "totalNota"), campo(raiz, "chaveAcesso"), List.copyOf(linhas));
        } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException falha) {
            throw invalida();
        }
    }

    private static void campos(JsonNode node, Set<String> esperados) {
        if (node == null || !node.isObject() || node.size() != esperados.size()) throw invalida();
        for (String nome : esperados) if (!node.has(nome)) throw invalida();
    }

    private static String campo(JsonNode node, String nome) {
        JsonNode valor = node.get(nome);
        if (valor.isNull()) return null;
        if (!valor.isTextual() || valor.textValue().length() > 500) throw invalida();
        return valor.textValue();
    }

    private static ExtracaoNotaException invalida() {
        return new ExtracaoNotaException(CodigoFalha.EXTRACAO_INVALIDA);
    }
}
