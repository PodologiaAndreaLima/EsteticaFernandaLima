package lima.fernanda.esteticaFernandaLima.mensagem.provider;

import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;
import lima.fernanda.esteticaFernandaLima.mensagem.whatsapp.WhatsAppLink;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Envio manual pelo WhatsApp: o backend não dispara a mensagem; ele registra o
 * histórico e devolve o link wa.me (MensagemResponse.whatsappUrl) para o app abrir.
 */
@Component
@ConditionalOnProperty(name = "mensagens.provider", havingValue = "whatsapp-link", matchIfMissing = true)
public class WhatsAppLinkMessageSender implements MessageSender {
    @Override
    public void send(CanalMensagem canal, String telefone, String conteudo) {
        if (canal != CanalMensagem.WHATSAPP) {
            throw new MessageProviderException("O envio manual só suporta o canal WHATSAPP");
        }
        if (WhatsAppLink.normalizarTelefone(telefone) == null) {
            throw new MessageProviderException("Telefone do cliente inválido para o WhatsApp");
        }
    }
}
