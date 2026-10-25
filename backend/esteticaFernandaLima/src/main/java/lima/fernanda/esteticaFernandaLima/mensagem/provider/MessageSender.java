package lima.fernanda.esteticaFernandaLima.mensagem.provider;

import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;

public interface MessageSender {
    void send(CanalMensagem canal, String telefone, String conteudo);
}
