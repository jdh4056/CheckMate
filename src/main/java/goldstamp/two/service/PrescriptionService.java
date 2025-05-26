package goldstamp.two.service;

import goldstamp.two.domain.*;
import goldstamp.two.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final MemberRepositoryClass memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public Long createPrescriptionByDiseaseName(Long memberId, String diseaseName) {

        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);

        // 2. 질병 조회
        Disease disease = diseaseRepository.findByName(diseaseName);

        // 3. 해당 질병 이름이 들어간 약 효능으로 약 조회
        List<Medicine> medicines = medicineRepository.findByEfficientContaining(disease.getName());
        // 4. 질병을 중간 데이터로 변경
        List<PrescriptionMedicine> prescriptionMedicines = medicines.stream()
                .map(medicine -> {
                    PrescriptionMedicine pm = new PrescriptionMedicine();
                    pm.setMedicine(medicine);
                    // 복용량, 복용시간 등 추가 정보가 있다면 여기서 세팅
                    return pm;
                })
                .collect(Collectors.toList());

        // 4. 처방전 생성 (정적 팩토리 메서드 사용)
        Prescription prescription = Prescription.createPrescription(member, disease, prescriptionMedicines.toArray(new PrescriptionMedicine[0]));

        // 5. 저장
        prescriptionRepository.save(prescription);

        return prescription.getId();
    }
}
