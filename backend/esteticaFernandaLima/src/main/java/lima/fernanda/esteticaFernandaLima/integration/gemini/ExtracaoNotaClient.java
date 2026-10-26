package lima.fernanda.esteticaFernandaLima.integration.gemini;

public interface ExtracaoNotaClient {
    ExtracaoNota extrair(byte[] imagem, String mime) throws ExtracaoNotaException;
}
