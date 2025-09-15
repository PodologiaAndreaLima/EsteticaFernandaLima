package school.sptech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Procedimento;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
}
