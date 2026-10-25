package lima.fernanda.esteticaFernandaLima.mensagem.whatsapp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Monta o link "click to chat" do WhatsApp (https://wa.me/NUMERO?text=MENSAGEM).
 * O app abre esse link e o WhatsApp do celular já aparece na conversa do cliente
 * com a mensagem digitada; o envio final é feito manualmente pelo usuário.
 */
public final class WhatsAppLink {
    private static final String DDI_BRASIL = "55";

    private WhatsAppLink() {}

    /** Mantém só os dígitos e acrescenta o DDI 55 quando o número vier só com DDD + número. */
    public static String normalizarTelefone(String telefone) {
        if (telefone == null) return null;
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("0")) digitos = digitos.replaceFirst("^0+", "");
        if (digitos.length() == 10 || digitos.length() == 11) digitos = DDI_BRASIL + digitos;
        return digitos.length() >= 12 && digitos.length() <= 13 ? digitos : null;
    }

    /** Retorna null quando o telefone não é válido para o WhatsApp. */
    public static String montar(String telefone, String mensagem) {
        String numero = normalizarTelefone(telefone);
        if (numero == null) return null;
        String url = "https://wa.me/" + numero;
        if (mensagem == null || mensagem.isBlank()) return url;
        return url + "?text=" + URLEncoder.encode(mensagem, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
