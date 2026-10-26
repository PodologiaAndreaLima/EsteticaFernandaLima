package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.CodigoFalha;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNota;
import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

public final class NormalizadorExtracaoNota {
    public record Linha(String texto, Integer quantidade, BigDecimal unitario, BigDecimal total, boolean valida) { }
    public record Nota(String cnpj, String fornecedor, LocalDate data, BigDecimal total,
                       String chave, List<Linha> linhas, List<String> avisos) { }

    private final Clock relogio;

    public NormalizadorExtracaoNota(Clock relogio) { this.relogio = relogio; }

    public Nota validar(ExtracaoNota extracao) {
        if (extracao == null) throw erro(CodigoFalha.EXTRACAO_INVALIDA);
        if (!extracao.legivel()) throw erro(CodigoFalha.IMAGEM_ILEGIVEL);
        LocalDate data = data(extracao.dataEmissao());
        BigDecimal total = dinheiro(extracao.totalNota());
        if (total == null || total.signum() <= 0 || extracao.itens() == null || extracao.itens().isEmpty()
                || extracao.itens().size() > 100) throw erro(CodigoFalha.NOTA_INVALIDA);
        String chave = digitos(extracao.chaveAcesso(), 44);
        if (chave != null && !chaveValida(chave)) throw erro(CodigoFalha.NOTA_INVALIDA);
        String nome = extracao.fornecedor() == null ? null : texto(extracao.fornecedor().razaoSocial(), 200);
        String cnpj = extracao.fornecedor() == null ? null : cnpj(extracao.fornecedor().cnpj());
        List<String> avisos = new ArrayList<>();
        if (nome == null) avisos.add("FORNECEDOR_AUSENTE");
        if (cnpj == null || !cnpjValido(cnpj)) { cnpj = null; avisos.add("CNPJ_INVALIDO"); }
        List<Linha> linhas = extracao.itens().stream().map(this::linha).toList();
        if (linhas.stream().allMatch(l -> !l.valida())) throw erro(CodigoFalha.NOTA_INVALIDA);
        if (linhas.stream().allMatch(l -> l.total() != null)) {
            BigDecimal soma = linhas.stream().map(Linha::total).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (soma.subtract(total).abs().compareTo(new BigDecimal("0.02")) > 0) avisos.add("TOTAL_DIVERGENTE");
        }
        return new Nota(cnpj, nome, data, total, chave, linhas, List.copyOf(avisos));
    }

    private Linha linha(ExtracaoNota.Item item) {
        if (item == null) throw erro(CodigoFalha.EXTRACAO_INVALIDA);
        String descricao = texto(item.texto(), 500);
        Integer quantidade = quantidade(item.quantidade());
        BigDecimal unitario = dinheiro(item.valorUnitario());
        BigDecimal total = dinheiro(item.valorTotal());
        boolean valida = descricao != null && quantidade != null && unitario != null && total != null
                && unitario.signum() > 0 && total.signum() > 0
                && unitario.multiply(BigDecimal.valueOf(quantidade)).subtract(total).abs()
                        .compareTo(new BigDecimal("0.02")) <= 0;
        return new Linha(descricao == null ? "Item sem descricao" : descricao, quantidade, unitario, total, valida);
    }

    private LocalDate data(String valor) {
        try {
            if (valor == null) throw new IllegalArgumentException();
            LocalDate dia = LocalDate.parse(valor, DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT));
            if (dia.isAfter(LocalDate.now(relogio))) throw new IllegalArgumentException();
            return dia;
        } catch (RuntimeException falha) { throw erro(CodigoFalha.NOTA_INVALIDA); }
    }

    private static BigDecimal dinheiro(String valor) {
        if (valor == null || valor.length() > 20) return null;
        String normal;
        if (valor.matches("[0-9]{1,3}(\\.[0-9]{3})+,[0-9]{2}")) normal = valor.replace(".", "").replace(',', '.');
        else if (valor.matches("(0|[1-9][0-9]{0,6})\\.[0-9]{2}")) normal = valor;
        else return null;
        BigDecimal numero = new BigDecimal(normal);
        return numero.compareTo(new BigDecimal("9999999.99")) <= 0 ? numero : null;
    }

    private static Integer quantidade(String valor) {
        if (valor == null || !valor.matches("[1-9][0-9]{0,8}")) return null;
        return Integer.valueOf(valor);
    }

    private static String texto(String valor, int limite) {
        if (valor == null || valor.isBlank() || valor.length() > limite ||
                !valor.matches("[\\p{L}\\p{N} .,;:/()%-]+")) return null;
        return valor.strip();
    }

    private static String digitos(String valor, int tamanho) {
        if (valor == null) return null;
        String numero = valor.replaceAll("[ ./-]", "");
        if (!numero.matches("[0-9]{" + tamanho + "}")) throw erro(CodigoFalha.NOTA_INVALIDA);
        return numero;
    }

    private static String cnpj(String valor) {
        if (valor == null) return null;
        String numero = valor.replaceAll("[ ./-]", "");
        return numero.matches("[0-9]{14}") ? numero : null;
    }

    private static boolean cnpjValido(String numero) {
        if (numero.chars().distinct().count() == 1) return false;
        for (int pos = 12; pos < 14; pos++) {
            int soma = 0;
            for (int i = 0; i < pos; i++) soma += (numero.charAt(i) - '0') * ((pos - i - 1) % 8 + 2);
            int resto = soma % 11;
            if (numero.charAt(pos) - '0' != (resto < 2 ? 0 : 11 - resto)) return false;
        }
        return true;
    }

    private static boolean chaveValida(String numero) {
        int soma = 0;
        for (int i = 0; i < 43; i++) soma += (numero.charAt(i) - '0') * (2 + (42 - i) % 8);
        int digito = 11 - soma % 11;
        return numero.charAt(43) - '0' == (digito >= 10 ? 0 : digito);
    }

    private static ExtracaoNotaException erro(CodigoFalha codigo) { return new ExtracaoNotaException(codigo); }
}
