package lima.fernanda.esteticaFernandaLima.mensagem.provider;

import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(name = "mensagens.provider", havingValue = "fake")
public class FakeMessageSender implements MessageSender {
    private final boolean fail;
    private final List<String> sentMessages = new ArrayList<>();

    public FakeMessageSender(@Value("${mensagens.fake-provider.fail:false}") boolean fail) {
        this.fail = fail;
    }

    @Override
    public synchronized void send(CanalMensagem canal, String telefone, String conteudo) {
        if (fail) throw new MessageProviderException("Falha simulada no provedor de mensagens");
        sentMessages.add(canal + ":" + telefone + ":" + conteudo);
    }

    public synchronized List<String> getSentMessages() {
        return Collections.unmodifiableList(new ArrayList<>(sentMessages));
    }
}
