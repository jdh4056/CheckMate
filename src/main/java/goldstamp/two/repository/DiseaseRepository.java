package goldstamp.two.repository;

import goldstamp.two.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository extends JpaRepository <Disease, Long> {
    // 이름이 정확히 일치
    Disease findByName(String name);

    List<Disease> findByNameContainingIgnoreCase(String name);

}
