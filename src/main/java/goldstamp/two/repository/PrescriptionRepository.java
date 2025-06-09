package goldstamp.two.repository;

import goldstamp.two.domain.Prescription;
import org.springframework.data.jpa.repository.EntityGraph; // EntityGraph 임포트 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // @Modifying 임포트 추가
import org.springframework.data.jpa.repository.Query; // @Query 임포트 추가

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 처방전 ID로 조회
    Optional<Prescription> findById(Long id);

    // 질병 이름으로 처방전 리스트 조회
    List<Prescription> findByDisease_Name(String name);

    // Member ID로 처방전 리스트 조회 시 필요한 연관 관계를 즉시 로딩하도록 EntityGraph 추가
    @EntityGraph(attributePaths = {"disease", "prescriptionMedicines", "prescriptionMedicines.medicine"})
    List<Prescription> findByMember_Id(Long memberId);

    // Member ID와 질병 이름으로 처방전 리스트 조회 (추가)
    @EntityGraph(attributePaths = {"disease", "prescriptionMedicines", "prescriptionMedicines.medicine"})
    List<Prescription> findByMember_IdAndDisease_NameContainingIgnoreCase(Long memberId, String diseaseName);

    @Modifying // DELETE, UPDATE 쿼리 시 필요
    @Query("delete from Prescription p where p.member.id = :memberId")
    void deleteByMember_Id(Long memberId);
}