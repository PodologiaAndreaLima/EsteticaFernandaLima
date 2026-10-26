package lima.fernanda.esteticaFernandaLima.integration.gemini;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public final class ChaveGeminiDisponivel implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String chave = context.getEnvironment().getProperty("GEMINI_API_KEY");
        return chave != null && !chave.isBlank();
    }
}
