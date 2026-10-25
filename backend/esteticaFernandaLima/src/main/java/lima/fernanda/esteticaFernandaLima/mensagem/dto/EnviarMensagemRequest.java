package lima.fernanda.esteticaFernandaLima.mensagem.dto;

import jakarta.validation.constraints.Size;
import lima.fernanda.esteticaFernandaLima.mensagem.model.CanalMensagem;

public record EnviarMensagemRequest(
        CanalMensagem canal,
        @Size(max = 2000) String conteudo
) {}
