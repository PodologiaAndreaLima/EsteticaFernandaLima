package lima.fernanda.esteticaFernandaLima.mensagem.repository;

import lima.fernanda.esteticaFernandaLima.mensagem.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    Page<Mensagem> findByClinicaId(String clinicaId, Pageable pageable);
    Page<Mensagem> findByClinicaIdAndLembreteId(String clinicaId, Long lembreteId, Pageable pageable);
}
