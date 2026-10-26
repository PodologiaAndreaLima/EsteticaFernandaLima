package lima.fernanda.esteticaFernandaLima.integration.gemini;

import java.util.List;

public record ExtracaoNota(boolean legivel, String motivo, Fornecedor fornecedor, String dataEmissao,
                           String totalNota, String chaveAcesso, List<Item> itens) {
    public record Fornecedor(String cnpj, String razaoSocial) { }
    public record Item(String texto, String quantidade, String valorUnitario, String valorTotal) { }
}
