package goldstamp.two.repository;

import goldstamp.two.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    // 효능에 특정 단어가 포함된 경우 (부분 검색)
    List<Medicine> findByEfficientContaining(String keyword);
}