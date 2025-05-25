package goldstamp.two.repository;

import goldstamp.two.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository <Disease, Long> {
}
