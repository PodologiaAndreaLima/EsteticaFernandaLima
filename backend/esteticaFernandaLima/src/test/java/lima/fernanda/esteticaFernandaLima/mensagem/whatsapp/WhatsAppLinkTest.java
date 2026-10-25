package lima.fernanda.esteticaFernandaLima.mensagem.whatsapp;

import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;
import lima.fernanda.esteticaFernandaLima.mensagem.provider.MessageProviderException;
import lima.fernanda.esteticaFernandaLima.mensagem.provider.WhatsAppLinkMessageSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhatsAppLinkTest {

    @Test
    void acrescentaDdiQuandoTelefoneVemSoComDddENumero() {
        assertEquals("5511986663333", WhatsAppLink.normalizarTelefone("(11) 98666-3333"));
        assertEquals("551132221111", WhatsAppLink.normalizarTelefone("11 3222-1111"));
    }

    @Test
    void mantemTelefoneQueJaTemDdi() {
        assertEquals("5511986663333", WhatsAppLink.normalizarTelefone("+55 11 98666-3333"));
    }

    @Test
    void retornaNuloParaTelefoneInvalido() {
        assertNull(WhatsAppLink.normalizarTelefone("123"));
        assertNull(WhatsAppLink.normalizarTelefone(null));
        assertNull(WhatsAppLink.montar("", "Olá"));
    }

    @Test
    void montaLinkComNumeroEMensagemCodificada() {
        String url = WhatsAppLink.montar("(11) 98666-3333", "Olá, Carlos! Até amanhã");

        assertTrue(url.startsWith("https://wa.me/5511986663333?text="));
        assertTrue(url.contains("Ol%C3%A1"));
        assertTrue(url.contains("%20"));
        assertFalse(url.contains("+"));
    }

    @Test
    void senderManualRecusaTelefoneInvalidoEOutrosCanais() {
        WhatsAppLinkMessageSender sender = new WhatsAppLinkMessageSender();

        assertDoesNotThrow(() -> sender.send(CanalMensagem.WHATSAPP, "11986663333", "Oi"));
        assertThrows(MessageProviderException.class, () -> sender.send(CanalMensagem.WHATSAPP, "123", "Oi"));
        assertThrows(MessageProviderException.class, () -> sender.send(CanalMensagem.SMS, "11986663333", "Oi"));
    }
}
