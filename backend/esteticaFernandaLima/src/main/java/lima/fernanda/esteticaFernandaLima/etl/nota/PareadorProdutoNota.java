package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.dto.nota.NotaContrato.Confianca;
import lima.fernanda.esteticaFernandaLima.model.ServicoProduto;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/** A suggestion is only a hint for review; this class never changes inventory. */
final class PareadorProdutoNota {
    record Sugestao(ServicoProduto produto, Confianca confianca) { }

    private static final Pattern MEDIDA = Pattern.compile("(?<![0-9])([0-9]+(?:[.,][0-9]+)?)\\s*(ml|mg|kg|l|g|%)(?![a-z0-9])");
    private static final Set<String> DESCARTAR = Set.of("de", "da", "do", "com", "para", "un", "und", "unidade", "produto");

    Sugestao sugerir(String texto, List<ServicoProduto> candidatos) {
        if (texto == null || texto.isBlank() || candidatos == null) return null;
        String origem = normalizar(texto);
        Set<String> marcas = new HashSet<>();
        for (ServicoProduto p : candidatos) {
            if (elegivel(p) && p.getMarca() != null && !p.getMarca().isBlank()) marcas.add(normalizar(p.getMarca()));
        }
        ServicoProduto melhor = null;
        double pontuacao = 0;
        boolean empate = false;
        for (ServicoProduto p : candidatos) {
            if (!elegivel(p) || p.getNome() == null || p.getNome().isBlank()) continue;
            String nome = normalizar(p.getNome());
            String marca = p.getMarca() == null ? "" : normalizar(p.getMarca());
            if (!marca.isBlank() && !contem(origem, marca)) continue;
            if (marcas.stream().anyMatch(m -> !m.equals(marca) && contem(origem, m))) continue;
            if (!medidasCompativeis(origem, nome + " " + marca)) continue;
            Set<String> a = tokens(origem);
            Set<String> b = tokens(nome + " " + marca);
            if (a.isEmpty() || b.isEmpty()) continue;
            Set<String> comuns = new HashSet<>(a);
            comuns.retainAll(b);
            double nota = (double) comuns.size() / Math.max(a.size(), b.size());
            if (nota < 0.6) continue;
            if (nota > pontuacao) { melhor = p; pontuacao = nota; empate = false; }
            else if (nota == pontuacao) empate = true;
        }
        return melhor == null || empate ? null : new Sugestao(melhor, pontuacao >= 0.85 ? Confianca.ALTA : Confianca.MEDIA);
    }

    private static boolean elegivel(ServicoProduto p) {
        return p != null && p.getIdProdutoServico() != null && Boolean.TRUE.equals(p.getProduto())
                && Boolean.TRUE.equals(p.getAtivo());
    }

    private static boolean contem(String texto, String termo) {
        return !termo.isBlank() && (" " + texto + " ").contains(" " + termo + " ");
    }

    private static String normalizar(String valor) {
        String simples = Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT).replaceAll("([0-9])\\s+(ml|mg|kg|l|g)\\b", "$1$2")
                .replaceAll("[^a-z0-9%,.]+", " ").trim();
        return simples.replaceAll("\\s+", " ");
    }

    private static Set<String> tokens(String texto) {
        Set<String> resultado = new HashSet<>(Arrays.asList(texto.replaceAll("[, .]", " ").split("\\s+")));
        resultado.removeAll(DESCARTAR);
        resultado.remove("");
        return resultado;
    }

    private static boolean medidasCompativeis(String origem, String candidato) {
        var medidasOrigem = MEDIDA.matcher(origem);
        while (medidasOrigem.find()) {
            var medidasCandidato = MEDIDA.matcher(candidato);
            boolean encontrada = false;
            while (medidasCandidato.find()) {
                if (medidasOrigem.group(2).equals(medidasCandidato.group(2))) {
                    encontrada = true;
                    if (!medidasOrigem.group(1).replace(',', '.').equals(medidasCandidato.group(1).replace(',', '.')))
                        return false;
                }
            }
            if (!encontrada) return false;
        }
        return true;
    }
}
