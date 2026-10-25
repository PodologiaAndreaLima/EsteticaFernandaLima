package lima.fernanda.esteticaFernandaLima.mensagem.generator;

import lima.fernanda.esteticaFernandaLima.mensagem.model.Lembrete;

public interface MessageGenerator {
    String generate(Lembrete lembrete);

    default String generate(Lembrete lembrete, MessageGenerationContext context) {
        return generate(lembrete);
    }
}
