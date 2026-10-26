package lima.fernanda.esteticaFernandaLima.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "nota")
public record NotaProperties(@Valid @NotNull Gemini gemini,
                             @Valid @NotNull Imagem imagem,
                             @Valid @NotNull Validacao validacao,
                             @Valid @NotNull Worker worker,
                             @Valid @NotNull RateLimit rateLimit) {

    public NotaProperties {
        if (gemini == null || worker == null || worker.lease().compareTo(gemini.timeout()) <= 0) {
            throw new IllegalArgumentException("nota.worker.lease deve superar nota.gemini.timeout");
        }
        if (rateLimit == null || rateLimit.uploadsPorMinuto() > rateLimit.requisicoesPorMinuto()) {
            throw new IllegalArgumentException("nota.rate-limit.uploads-por-minuto deve respeitar o limite geral");
        }
    }

    public record Gemini(@NotBlank String modelo, @NotNull Duration timeout) {
        public Gemini {
            if (timeout == null || timeout.isNegative() || timeout.isZero() || timeout.compareTo(Duration.ofMinutes(2)) > 0) {
                throw new IllegalArgumentException("nota.gemini.timeout deve estar entre 1ns e 2min");
            }
        }
    }

    public record Imagem(@Min(1) @Max(10_485_760) long maxBytes,
                         @Min(1) @Max(20_000_000) long maxPixels,
                         @NotNull Path diretorioPrivado) {
        public Imagem {
            if (diretorioPrivado == null || !diretorioPrivado.isAbsolute()) {
                throw new IllegalArgumentException("nota.imagem.diretorio-privado deve ser absoluto");
            }
        }
    }

    public record Validacao(@NotNull @DecimalMin("0.01") @DecimalMax("1.00") BigDecimal toleranciaTotal) {
    }

    public record Worker(@Min(1) @Max(4) int concorrencia,
                          @Min(1) @Max(3) int maxTentativas,
                         @NotNull Duration lease) {
        public Worker {
            if (lease == null || lease.isNegative() || lease.isZero() || lease.compareTo(Duration.ofMinutes(10)) > 0) {
                throw new IllegalArgumentException("nota.worker.lease deve estar entre 1ns e 10min");
            }
        }
    }

    public record RateLimit(@Min(1) @Max(120) int requisicoesPorMinuto,
                            @Min(1) @Max(20) int uploadsPorMinuto) {
    }
}
