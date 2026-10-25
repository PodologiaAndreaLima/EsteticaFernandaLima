package lima.fernanda.esteticaFernandaLima.mensagem.mapper;

import lima.fernanda.esteticaFernandaLima.mensagem.dto.*;
import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import org.springframework.stereotype.Component;

@Component
public class MensagemMapper {
    public LembreteResponse toResponse(Lembrete l) {
        return new LembreteResponse(l.getId(), l.getClinicaId(), l.getCliente().getId(),
                l.getCliente().getNomeCompleto(), l.getAgendamentoId(), l.getDataHora(), l.getTipo(),
                l.getStatus(), l.getObservacao(), l.getDataUltimoAtendimento(),
                l.getDataRetornoPrevisto(), l.getProcedimento(), l.getCriadoEm());
    }
    public MensagemResponse toResponse(Mensagem m) {
        return new MensagemResponse(m.getId(), m.getClinicaId(), m.getLembrete().getId(),
                m.getLembrete().getCliente().getId(), m.getCanal(), m.getConteudo(),
                m.getTelefoneDestino(), m.getStatus(), m.getErroProvedor(), m.getCriadoEm(), m.getEnviadoEm());
    }
}
