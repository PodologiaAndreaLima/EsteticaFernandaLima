package lima.fernanda.esteticaFernandaLima.mensagem.generator;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MessageGenerationContext(
        String nomeCliente,
        String procedimento,
        LocalDateTime dataHoraAtendimento,
        LocalDate dataUltimoAtendimento,
        LocalDate dataRetornoPrevisto,
        String observacao,
        boolean dadosDaOrdemServicoDisponiveis
) {}
