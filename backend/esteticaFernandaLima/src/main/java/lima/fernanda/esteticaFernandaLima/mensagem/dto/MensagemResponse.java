package lima.fernanda.esteticaFernandaLima.mensagem.dto;

import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import java.time.LocalDateTime;

public record MensagemResponse(Long id, String clinicaId, Long lembreteId, Integer clienteId,
                               CanalMensagem canal, String conteudo, String telefoneDestino,
                               StatusMensagem status, String erroProvedor,
                               LocalDateTime criadoEm, LocalDateTime enviadoEm) {}
