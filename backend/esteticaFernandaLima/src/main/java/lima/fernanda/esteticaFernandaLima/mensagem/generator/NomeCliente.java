package lima.fernanda.esteticaFernandaLima.mensagem.generator;

/**
 * As mensagens tratam a cliente pelo primeiro nome: soa mais natural e reduz
 * o dado pessoal enviado para a OpenAI (o sobrenome não é necessário).
 */
public final class NomeCliente {
    private NomeCliente() {}

    public static String primeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) return nomeCompleto;
        return nomeCompleto.trim().split("\\s+")[0];
    }
}
