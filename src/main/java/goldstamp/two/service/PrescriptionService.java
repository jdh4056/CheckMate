package goldstamp.two.service;

import goldstamp.two.domain.*;
import goldstamp.two.dto.PrescriptionRequestDto;
import goldstamp.two.dto.PrescriptionMedicineRequestDto; // PrescriptionMedicineRequestDto 임포트 추가
import goldstamp.two.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final DiseaseService diseaseService; // DiseaseService 주입 추가


    @Transactional
    public Long createEmptyPrescription(Long memberId, LocalDate prescriptionDate) {
        log.info("createEmptyPrescription 호출됨. memberId: {}, prescriptionDate: {}", memberId, prescriptionDate);
        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
        log.debug("멤버 조회 성공: {}", member.getName());

        // 2. 빈 처방전 생성 (질병 및 약 없이)
        Prescription prescription = Prescription.createEmptyPrescription(member, prescriptionDate);
        prescription.setName("미지정 질병");
        prescription.setDescription("설명 없음");

        // 3. 저장
        prescriptionRepository.save(prescription);
        log.info("빈 처방전 생성 및 저장 성공. prescriptionId: {}", prescription.getId());

        return prescription.getId();
    }

    @Transactional
    public Long createPrescriptionByDiseaseName(Long memberId, PrescriptionRequestDto requestDto) {
        log.info("createPrescriptionByDiseaseName 호출됨. memberId: {}, diseaseName: {}, prescriptionDate: {}",
                memberId, requestDto.getDiseaseName(), requestDto.getPrescriptionDate());

        // 1. 멤버 조회
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
        log.debug("멤버 조회 성공: {}", member.getName());

        // 2. 질병 조회 (단일 결과가 보장되지 않으므로 List로 받고 첫 번째를 사용)
        List<Disease> diseases = diseaseRepository.findByNameContainingIgnoreCase(requestDto.getDiseaseName());
        if (diseases.isEmpty()) {
            log.error("질병을 찾을 수 없습니다. diseaseName: {}", requestDto.getDiseaseName());
            throw new IllegalArgumentException("Disease not found with name: " + requestDto.getDiseaseName());
        }
        Disease disease = diseases.get(0);
        log.debug("질병 조회 성공: {}, Explain: {}", disease.getName(), disease.getExplain());

        // 3. 해당 질병 이름이 들어간 약 효능으로 약 조회 (여기서는 PrescriptionMedicine에 상세 정보가 없으므로 기본값으로 설정)
        // 이 부분은 createPrescriptionByDiseaseName의 목적이 질병 기반 처방전 생성임을 고려하여,
        // 약 정보는 나중에 addMedicineToPrescription으로 추가하는 것이 더 적절할 수 있습니다.
        // 현재는 기존 로직을 유지하되, PrescriptionMedicine에 추가된 필드는 기본값으로 설정합니다.
        List<Medicine> medicines = medicineRepository.findByEfficientContaining(disease.getName());
        log.debug("질병 효능으로 찾은 약 개수: {}", medicines.size());

        List<PrescriptionMedicine> prescriptionMedicines = medicines.stream()
                .map(medicine -> {
                    PrescriptionMedicine pm = new PrescriptionMedicine();
                    pm.setMedicine(medicine);
                    // 새로 추가된 필드들은 기본값으로 설정하거나, 필요에 따라 DTO를 통해 받을 수 있도록 확장
                    // 복약 시작일은 질병 진단 날짜와 동일하게 설정
                    pm.setStartDate(requestDto.getPrescriptionDate());
                    pm.setEndDate(null); // 또는 LocalDate.now().plusDays(7)
                    pm.setNumPerDay(0); // Integer 타입으로 변경되었으므로 0으로 초기화 가능
                    pm.setAlarmTimer1(null);
                    pm.setAlarmTimer2(null);
                    pm.setAlarmTimer3(null);
                    pm.setAlarmTimer4(null);
                    pm.setDose(null); // dose 기본값 설정
                    pm.setDoseType(null); // doseType 기본값 설정
                    pm.calculateTotalDrugNum(); // totalDrugNum 계산
                    return pm;
                })
                .collect(Collectors.toList());
        log.debug("PrescriptionMedicine 리스트 생성 완료. 개수: {}", prescriptionMedicines.size());

        // 4. 처방전 생성 (정적 팩토리 메서드 사용) - prescriptionDate 전달
        Prescription prescription = Prescription.createPrescription(
                member,
                disease,
                requestDto.getPrescriptionDate(),
                prescriptionMedicines.toArray(new PrescriptionMedicine[0]));
        prescription.setName(disease.getName());
        prescription.setDescription(disease.getExplain());
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

    // 질병 이름으로 처방전을 찾는 메서드 추가
    public List<Prescription> findPrescriptionsByDiseaseName(Long memberId, String diseaseName) {
        log.info("findPrescriptionsByDiseaseName 호출됨. memberId: {}, diseaseName: {}", memberId, diseaseName);
        return prescriptionRepository.findByMember_IdAndDisease_NameContainingIgnoreCase(memberId, diseaseName);
    }

    @Transactional // 트랜잭션 추가
    public Long findOrCreatePrescriptionForDisease(Long memberId, String diseaseName, LocalDate prescriptionDate) {
        log.info("findOrCreatePrescriptionForDisease 호출됨. memberId: {}, diseaseName: {}", memberId, diseaseName);

        // 1. 해당 질병명으로 처방전이 있는지 확인
        List<Prescription> existingPrescriptions = prescriptionRepository.findByMember_IdAndDisease_NameContainingIgnoreCase(memberId, diseaseName);
        if (!existingPrescriptions.isEmpty()) {
            log.info("Existing prescription for disease '{}' found for member {}. ID: {}", diseaseName, memberId, existingPrescriptions.get(0).getId());
            return existingPrescriptions.get(0).getId();
        }

        // 2. 멤버 조회
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            log.error("멤버를 찾을 수 없습니다. memberId: {}", memberId);
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }
        log.debug("멤버 조회 성공: {}", member.getName());

        // 3. 질병 조회 또는 생성
        // diseaseRepository.findOrCreateDisease(diseaseName) 대신 diseaseService.findOrCreateDisease(diseaseName) 호출
        Disease disease = diseaseService.findOrCreateDisease(diseaseName); // DiseaseService의 findOrCreateDisease 사용
        log.debug("질병 조회 또는 생성 성공: {}", disease.getName());

        // 4. 새로운 처방전 생성
        Prescription newPrescription = Prescription.createEmptyPrescription(member, prescriptionDate);
        newPrescription.setDisease(disease);
        newPrescription.setName(disease.getName());
        newPrescription.setDescription(disease.getExplain());

        // 5. 저장
        prescriptionRepository.save(newPrescription);
        log.info("New prescription for disease '{}' created for member {}. ID: {}", diseaseName, memberId, newPrescription.getId());
        return newPrescription.getId();
    }


    @Transactional
    public void addDiseaseToPrescription(Long memberId, Long prescriptionId, String diseaseName) {
        log.info("addDiseaseToPrescription 호출됨. memberId: {}, prescriptionId: {}, diseaseName: {}", memberId, prescriptionId, diseaseName);

        log.debug("처방전 조회 시도. prescriptionId: {}", prescriptionId);
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> {
                    log.error("처방전을 찾을 수 없습니다. prescriptionId: {}", prescriptionId);
                    return new IllegalArgumentException("Prescription not found with ID: " + prescriptionId);
                });
        log.debug("처방전 조회 성공. prescriptionId: {}", prescription.getId());

        log.debug("질병 조회 시도. diseaseName: {}", diseaseName);
        List<Disease> diseases = diseaseRepository.findByNameContainingIgnoreCase(diseaseName);
        if (diseases.isEmpty()) {
            log.error("질병을 찾을 수 없습니다. diseaseName: {}", diseaseName);
            throw new IllegalArgumentException("Disease not found with name: " + diseaseName);
        }
        Disease disease = diseases.get(0);
        log.info("질병 조회 성공: {}. 설명: {}", disease.getName(), disease.getExplain());

        prescription.setDisease(disease);
        prescription.setName(disease.getName());
        prescription.setDescription(disease.getExplain());
        log.info("처방전 업데이트 성공. 이름: {}, 설명: {}", prescription.getName(), prescription.getDescription());

        log.debug("트랜잭션 커밋 대기 중...");
    }

    @Transactional
    public void addMedicineToPrescription(Long memberId, Long prescriptionId, PrescriptionMedicineRequestDto requestDto) { // DTO로 변경
        log.info("addMedicineToPrescription 호출됨. memberId: {}, prescriptionId: {}, medicineName: {}",
                memberId, prescriptionId, requestDto.getMedicineName());

        log.debug("처방전 조회 시도. prescriptionId: {}", prescriptionId);
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> {
                    log.error("처방전을 찾을 수 없습니다. prescriptionId: {}", prescriptionId);
                    return new IllegalArgumentException("Prescription not found with ID: " + prescriptionId);
                });
        log.debug("처방전 조회 성공. prescriptionId: {}", prescription.getId());

        // 약 조회 (ID 또는 이름으로)
        Medicine medicine;
        if (requestDto.getMedicineId() != null) {
            medicine = medicineRepository.findById(requestDto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found with ID: " + requestDto.getMedicineId()));
        } else if (requestDto.getMedicineName() != null && !requestDto.getMedicineName().trim().isEmpty()) {
            List<Medicine> medicines = medicineRepository.findByMedicineNameContainingIgnoreCase(requestDto.getMedicineName());
            if (medicines.isEmpty()) {
                log.error("약을 찾을 수 없습니다. medicineName: {}", requestDto.getMedicineName());
                throw new IllegalArgumentException("Medicine not found with name: " + requestDto.getMedicineName());
            }
            medicine = medicines.get(0); // 첫 번째 약 사용
        } else {
            throw new IllegalArgumentException("Medicine ID or Name must be provided.");
        }
        log.info("약 조회 성공: {}", medicine.getMedicineName());

        // PrescriptionMedicine 객체 생성 및 처방전에 추가
        PrescriptionMedicine prescriptionMedicine = new PrescriptionMedicine();
        prescriptionMedicine.setMedicine(medicine);
        prescriptionMedicine.setStartDate(requestDto.getStartDate());
        prescriptionMedicine.setEndDate(requestDto.getEndDate());
        prescriptionMedicine.setNumPerDay(requestDto.getNumPerDay() != null ? requestDto.getNumPerDay() : 0); // null 체크 및 기본값 설정
        prescriptionMedicine.setAlarmTimer1(requestDto.getAlarmTimer1());
        prescriptionMedicine.setAlarmTimer2(requestDto.getAlarmTimer2());
        prescriptionMedicine.setAlarmTimer3(requestDto.getAlarmTimer3());
        prescriptionMedicine.setAlarmTimer4(requestDto.getAlarmTimer4());
        prescriptionMedicine.setDose(requestDto.getDose()); // dose 설정
        prescriptionMedicine.setDoseType(requestDto.getDoseType()); // doseType 설정
        // totalDrugNum은 setter에서 자동으로 계산됨 (PrescriptionMedicine 엔티티에서 처리)

        prescription.addMedicine(prescriptionMedicine);
        log.info("PrescriptionMedicine 추가 성공. 약: {}", medicine.getMedicineName());

        log.debug("트랜잭션 커밋 대기 중...");
    }

    @Transactional
    public void deletePrescription(Long prescriptionId, Long memberId) throws IllegalAccessException {
        log.info("deletePrescription 호출됨. prescriptionId: {}, memberId: {}", prescriptionId, memberId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));

        if (!Objects.equals(prescription.getMember().getId(), memberId)) {
            log.error("Attempt to delete prescription not owned by member. PrescriptionId: {}, MemberId: {}", prescriptionId, memberId);
            throw new IllegalAccessException("You are not authorized to delete this prescription.");
        }

        prescriptionRepository.delete(prescription);
        log.info("Prescription deleted successfully: ID={}", prescriptionId);
    }
}
