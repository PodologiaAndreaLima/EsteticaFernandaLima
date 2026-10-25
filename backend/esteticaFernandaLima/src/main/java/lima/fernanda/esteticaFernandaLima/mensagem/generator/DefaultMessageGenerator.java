package lima.fernanda.esteticaFernandaLima.mensagem.generator;

import lima.fernanda.esteticaFernandaLima.mensagem.model.Lembrete;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name = "mensagens.ai.enabled", havingValue = "false", matchIfMissing = true)
public class DefaultMessageGenerator implements MessageGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    @Override
    public String generate(Lembrete lembrete) {
        if (lembrete.getTipo() == lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem.RETORNO) {
            return "Olá, " + NomeCliente.primeiroNome(lembrete.getCliente().getNomeCompleto()) + "! "
                    + "Estamos entrando em contato para lembrar sobre seu retorno"
                    + (lembrete.getProcedimento() == null ? "" : " referente ao procedimento de " + lembrete.getProcedimento())
                    + ". Se desejar, podemos ajudar você a verificar a disponibilidade de horários.";
        }
        return "Olá, " + NomeCliente.primeiroNome(lembrete.getCliente().getNomeCompleto()) + "! "
                + "Este é um lembrete do seu atendimento em "
                + lembrete.getDataHora().format(FORMATTER) + ".";
    }

    @Override
    public String generate(Lembrete lembrete, MessageGenerationContext context) {
        return generate(lembrete);
    }
}
