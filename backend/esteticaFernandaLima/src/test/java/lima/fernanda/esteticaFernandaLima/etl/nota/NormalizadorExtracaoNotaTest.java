package lima.fernanda.esteticaFernandaLima.etl.nota;

import lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNota;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NormalizadorExtracaoNotaTest {
    private static final String CHAVE_VALIDA = "35261103476811001476650020000777921000376157";
    private final NormalizadorExtracaoNota normalizador =
            new NormalizadorExtracaoNota(Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC));

    private ExtracaoNota nota(String chave) {
        return new ExtracaoNota(true, null, new ExtracaoNota.Fornecedor("11222333000181", "DISTRIBUIDORA EXEMPLO LTDA"),
                "2026-10-21", "35.90", chave,
                List.of(new ExtracaoNota.Item("Creme hidratante 200g", "1", "35.90", "35.90")));
    }

    @Test
    void chaveValidaFicaNaNotaSemAviso() {
        var resultado = normalizador.validar(nota(CHAVE_VALIDA));
        assertEquals(CHAVE_VALIDA, resultado.chave());
        assertFalse(resultado.avisos().contains("CHAVE_NAO_CONFIRMADA"));
    }

    @Test
    void chaveComPontuacaoEEspacosEhNormalizada() {
        String formatada = CHAVE_VALIDA.substring(0, 22) + " " + CHAVE_VALIDA.substring(22);
        assertEquals(CHAVE_VALIDA, normalizador.validar(nota(formatada)).chave());
    }

    @Test
    void chaveComDigitoFaltandoNaoRejeitaANotaEGeraAviso() {
        var resultado = normalizador.validar(nota(CHAVE_VALIDA.substring(1)));
        assertNull(resultado.chave());
        assertTrue(resultado.avisos().contains("CHAVE_NAO_CONFIRMADA"));
        assertEquals(1, resultado.linhas().size());
    }

    @Test
    void chaveComDigitoVerificadorErradoNaoRejeitaANotaEGeraAviso() {
        String errada = CHAVE_VALIDA.substring(0, 43) + (CHAVE_VALIDA.charAt(43) == '0' ? '1' : '0');
        var resultado = normalizador.validar(nota(errada));
        assertNull(resultado.chave());
        assertTrue(resultado.avisos().contains("CHAVE_NAO_CONFIRMADA"));
    }

    @Test
    void chaveComLetrasEhTratadaComoNaoConfirmada() {
        var resultado = normalizador.validar(nota("ABC" + CHAVE_VALIDA.substring(3)));
        assertNull(resultado.chave());
        assertTrue(resultado.avisos().contains("CHAVE_NAO_CONFIRMADA"));
    }

    @Test
    void semChaveNenhumaNaoGeraAviso() {
        assertFalse(normalizador.validar(nota(null)).avisos().contains("CHAVE_NAO_CONFIRMADA"));
        assertFalse(normalizador.validar(nota("  ")).avisos().contains("CHAVE_NAO_CONFIRMADA"));
    }

    @Test
    void notaSemTotalContinuaSendoRejeitada() {
        var semTotal = new ExtracaoNota(true, null, null, "2026-10-21", null, null,
                List.of(new ExtracaoNota.Item("Creme", "1", "35.90", "35.90")));
        org.junit.jupiter.api.Assertions.assertThrows(
                lima.fernanda.esteticaFernandaLima.integration.gemini.ExtracaoNotaException.class,
                () -> normalizador.validar(semTotal));
    }
}
