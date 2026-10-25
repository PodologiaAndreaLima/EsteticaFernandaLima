package lima.fernanda.esteticaFernandaLima.mensagem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lima.fernanda.esteticaFernandaLima.mensagem.model.TipoMensagem;

import java.time.LocalDateTime;
import java.time.LocalDate;

public record CriarLembreteRequest(
        @Positive @NotNull Integer clienteId,
        @Positive Long agendamentoId,
        @NotNull LocalDateTime dataHora,
        @NotNull TipoMensagem tipo,
        @Size(max = 500) String observacao,
        LocalDate dataUltimoAtendimento,
        LocalDate dataRetornoPrevisto,
        @Size(max = 300) String procedimento,
        @Size(max = 100) String clinicaId
) {
    public CriarLembreteRequest(Integer clienteId, Long agendamentoId, LocalDateTime dataHora,
                                TipoMensagem tipo, String observacao, String clinicaId) {
        this(clienteId, agendamentoId, dataHora, tipo, observacao, null, null, null, clinicaId);
    }
}
