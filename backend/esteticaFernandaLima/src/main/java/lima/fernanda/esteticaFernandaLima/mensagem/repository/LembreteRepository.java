package lima.fernanda.esteticaFernandaLima.mensagem.repository;

import lima.fernanda.esteticaFernandaLima.mensagem.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    Page<Lembrete> findByClinicaId(String clinicaId, Pageable pageable);
    boolean existsByClinicaIdAndClienteIdAndDataHoraAndTipo(String clinicaId, Integer clienteId,
                                                             java.time.LocalDateTime dataHora, TipoMensagem tipo);
    java.util.Optional<Lembrete> findByIdAndClinicaId(Long id, String clinicaId);
}
