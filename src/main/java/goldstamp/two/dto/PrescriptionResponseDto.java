package goldstamp.two.dto;
import goldstamp.two.domain.Disease;
import goldstamp.two.domain.Medicine;
import goldstamp.two.domain.PrescriptionMedicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // LocalDate 임포트 추가
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate prescriptionDate; // prescriptionDate 필드 추가
    private DiseaseDto disease; // Disease 정보를 담을 DTO
    private List<PrescriptionMedicineResponseDto> medicines; // Medicine 정보를 담을 DTO 리스트 (타입 변경)

    // Prescription 엔티티를 DTO로 변환하는 팩토리 메서드
    public static PrescriptionResponseDto fromEntity(goldstamp.two.domain.Prescription prescription) {
        // Disease가 null이 아닐 경우에만 DiseaseDto 생성
        DiseaseDto diseaseDto = null;
        if (prescription.getDisease() != null) {
            diseaseDto = DiseaseDto.builder()
                    .id(prescription.getDisease().getId())
                    .name(prescription.getDisease().getName())
                    .explain(prescription.getDisease().getExplain())
                    .build();
        }

        // prescriptionMedicines 리스트를 PrescriptionMedicineResponseDto 리스트로 변환
        List<PrescriptionMedicineResponseDto> prescriptionMedicineDtos = prescription.getPrescriptionMedicines().stream()
                .map(pm -> PrescriptionMedicineResponseDto.fromEntity(pm)) // PrescriptionMedicineResponseDto의 fromEntity 사용
                .collect(Collectors.toList());

        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .name(prescription.getName())
                .description(prescription.getDescription())
                .prescriptionDate(prescription.getPrescriptionDate()) // 필드 값 설정
                .disease(diseaseDto)
                .medicines(prescriptionMedicineDtos) // 변경된 DTO 리스트 사용
                .build();
    }
}
