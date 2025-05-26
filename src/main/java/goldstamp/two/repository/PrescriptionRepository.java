package goldstamp.two.repository;

import goldstamp.two.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 처방전 ID로 조회
    Optional<Prescription> findById(Long id);

    // 질병 이름으로 처방전 리스트 조회
    List<Prescription> findByDisease_Name(String name);

}