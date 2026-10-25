package lima.fernanda.esteticaFernandaLima.mensagem.dto;

import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record LembreteResponse(Long id, String clinicaId, Integer clienteId, String nomeCliente,
                               Long agendamentoId, LocalDateTime dataHora, TipoMensagem tipo,
                               StatusLembrete status, String observacao, LocalDate dataUltimoAtendimento,
                               LocalDate dataRetornoPrevisto, String procedimento, LocalDateTime criadoEm) {}
