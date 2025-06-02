// front + back/back/src/main/java/goldstamp/two/service/PrescriptionService.java
package goldstamp.two.service;

import goldstamp.two.domain.*;
import goldstamp.two.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionService.class);

    private final MemberRepositoryClass memberRepository;
    private final DiseaseRepository diseaseRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;


    @Transactional
    public Long createEmptyPrescription(Long memberId) {
        log.info("createEmptyPrescription 호출됨. memberId: {}", memberId);
        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
        log.debug("멤버 조회 성공: {}", member.getName());

        // 2. 빈 처방전 생성 (질병 및 약 없이)
        Prescription prescription = new Prescription();
        prescription.setMember(member);
        prescription.setName("미지정 질병"); // 초기값 설정
        prescription.setDescription("설명 없음"); // 초기값 설정

        // 3. 저장
        prescriptionRepository.save(prescription);
        log.info("빈 처방전 생성 및 저장 성공. prescriptionId: {}", prescription.getId());

        return prescription.getId();
    }

    @Transactional
    public Long createPrescriptionByDiseaseName(Long memberId, String diseaseName) {
        log.info("createPrescriptionByDiseaseName 호출됨. memberId: {}, diseaseName: {}", memberId, diseaseName);

        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
        log.debug("멤버 조회 성공: {}", member.getName());

        // 2. 질병 조회 (단일 결과가 보장되지 않으므로 List로 받고 첫 번째를 사용)
        List<Disease> diseases = diseaseRepository.findByNameContainingIgnoreCase(diseaseName);
        if (diseases.isEmpty()) {
            log.error("질병을 찾을 수 없습니다. diseaseName: {}", diseaseName);
            throw new IllegalArgumentException("Disease not found with name: " + diseaseName);
        }
        Disease disease = diseases.get(0); // 첫 번째 질병 사용
        log.debug("질병 조회 성공: {}, Explain: {}", disease.getName(), disease.getExplain());

        // 3. 해당 질병 이름이 들어간 약 효능으로 약 조회
        List<Medicine> medicines = medicineRepository.findByEfficientContaining(disease.getName());
        log.debug("질병 효능으로 찾은 약 개수: {}", medicines.size());

        // 4. 질병을 중간 데이터로 변경
        List<PrescriptionMedicine> prescriptionMedicines = medicines.stream()
                .map(medicine -> {
                    PrescriptionMedicine pm = new PrescriptionMedicine();
                    pm.setMedicine(medicine);
                    return pm;
                })
                .collect(Collectors.toList());
        log.debug("PrescriptionMedicine 리스트 생성 완료. 개수: {}", prescriptionMedicines.size());

        // 4. 처방전 생성 (정적 팩토리 메서드 사용)
        Prescription prescription = Prescription.createPrescription(member, disease, prescriptionMedicines.toArray(new PrescriptionMedicine[0]));
        prescription.setName(disease.getName()); // 질병 이름으로 처방전 이름 설정
        prescription.setDescription(disease.getExplain()); // 질병 설명으로 처방전 설명 설정
        log.debug("새로운 처방전 객체 생성 및 이름/설명 설정 완료. Name: {}, Description: {}", prescription.getName(), prescription.getDescription());

        // 5. 저장
        prescriptionRepository.save(prescription);
        log.info("처방전 생성 및 저장 성공. prescriptionId: {}", prescription.getId());

        return prescription.getId();
    }

    // 진단서 ID로 하나의 진단서 조회 메서드 추가
    public Prescription findOnePrescription(Long prescriptionId) {
        log.info("findOnePrescription 호출됨. prescriptionId: {}", prescriptionId);
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));
    }

    // Member ID로 모든 처방전 조회 (새로 추가)
    public List<Prescription> findPrescriptionsByMemberId(Long memberId) {
        log.info("findPrescriptionsByMemberId 호출됨. memberId: {}", memberId);
        return prescriptionRepository.findByMember_Id(memberId);
    }

    @Transactional
    public void addDiseaseToPrescription(Long memberId, Long prescriptionId, String diseaseName) {
        log.info("addDiseaseToPrescription 호출됨. memberId: {}, prescriptionId: {}, diseaseName: {}", memberId, prescriptionId, diseaseName);

        // 1. 멤버와 처방전 유효성 검사 (선택 사항, 보안 강화)
        // 실제 애플리케이션에서는 memberId를 통해 해당 멤버가 prescriptionId의 소유자인지 확인하는 로직이 필요합니다.
        // 현재는 memberId를 단순히 경로 변수로 받지만, JWT 토큰 등에서 가져온 인증된 사용자 ID와 비교하는 것이 좋습니다.
        log.debug("처방전 조회 시도. prescriptionId: {}", prescriptionId);
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> {
                    log.error("처방전을 찾을 수 없습니다. prescriptionId: {}", prescriptionId);
                    return new IllegalArgumentException("Prescription not found with ID: " + prescriptionId);
                });
        log.debug("처방전 조회 성공. prescriptionId: {}", prescription.getId());


        // 3. 질병 조회 (단일 결과가 보장되지 않으므로 List로 받고 첫 번째를 사용)
        log.debug("질병 조회 시도. diseaseName: {}", diseaseName);
        List<Disease> diseases = diseaseRepository.findByNameContainingIgnoreCase(diseaseName);
        if (diseases.isEmpty()) {
            log.error("질병을 찾을 수 없습니다. diseaseName: {}", diseaseName);
            throw new IllegalArgumentException("Disease not found with name: " + diseaseName);
        }
        Disease disease = diseases.get(0); // 첫 번째 질병 사용
        log.info("질병 조회 성공: {}. 설명: {}", disease.getName(), disease.getExplain());

        // 4. 처방전에 질병 추가 (업데이트)
        prescription.setDisease(disease);
        prescription.setName(disease.getName());
        prescription.setDescription(disease.getExplain()); // Disease의 설명으로 업데이트
        log.info("처방전 업데이트 성공. 이름: {}, 설명: {}", prescription.getName(), prescription.getDescription());

        // 저장 (Transactional 어노테이션으로 인해 자동 더티 체킹되어 별도 save 호출 필요 없음)
        log.debug("트랜잭션 커밋 대기 중..."); // 디버그 로그 추가 (커밋 시점 예상)
    }

    @Transactional
    public void addMedicineToPrescription(Long memberId, Long prescriptionId, String medicineName) {
        log.info("addMedicineToPrescription 호출됨. memberId: {}, prescriptionId: {}, medicineName: {}", memberId, prescriptionId, medicineName);

        // 1. 멤버와 처방전 유효성 검사 (선택 사항, 보안 강화)
        log.debug("처방전 조회 시도. prescriptionId: {}", prescriptionId);
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> {
                    log.error("처방전을 찾을 수 없습니다. prescriptionId: {}", prescriptionId);
                    return new IllegalArgumentException("Prescription not found with ID: " + prescriptionId);
                });
        log.debug("처방전 조회 성공. prescriptionId: {}", prescription.getId());

        // 3. 약 조회 (단일 결과가 보장되지 않으므로 List로 받고 첫 번째를 사용)
        log.debug("약 조회 시도. medicineName: {}", medicineName);
        List<Medicine> medicines = medicineRepository.findByMedicineNameContainingIgnoreCase(medicineName);
        if (medicines.isEmpty()) {
            log.error("약을 찾을 수 없습니다. medicineName: {}", medicineName);
            throw new IllegalArgumentException("Medicine not found with name: " + medicineName);
        }
        Medicine medicine = medicines.get(0); // 정확히 일치하는 약을 찾거나, 여러 개 중 첫 번째를 가져옴
        log.info("약 조회 성공: {}", medicine.getMedicineName());

        // 4. PrescriptionMedicine 객체 생성 및 처방전에 추가
        PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
        prescriptionMedicine.setMedicine(medicine);
        prescription.addMedicine(prescriptionMedicine);
        log.info("PrescriptionMedicine 추가 성공. 약: {}", medicine.getMedicineName());

        // 저장 (Transactional 어노테이션으로 인해 자동 더티 체킹되어 별도 save 호출 필요 없음)
        log.debug("트랜잭션 커밋 대기 중..."); // 디버그 로그 추가 (커밋 시점 예상)
    }
    @Transactional
    public void deletePrescription(Long prescriptionId, Long memberId) throws IllegalAccessException {
        log.info("deletePrescription 호출됨. prescriptionId: {}, memberId: {}", prescriptionId, memberId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));

        // Verify that the prescription belongs to the member
        if (!Objects.equals(prescription.getMember().getId(), memberId)) {
            log.error("Attempt to delete prescription not owned by member. PrescriptionId: {}, MemberId: {}", prescriptionId, memberId);
            throw new IllegalAccessException("You are not authorized to delete this prescription.");
        }

        prescriptionRepository.delete(prescription);
        log.info("Prescription deleted successfully: ID={}", prescriptionId);
    }
}