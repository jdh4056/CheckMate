package goldstamp.two.dto;
import goldstamp.two.domain.Disease;
import goldstamp.two.domain.Medicine;
import goldstamp.two.domain.PrescriptionMedicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private DiseaseDto disease; // Disease 정보를 담을 DTO
    private List<MedicineDto> medicines; // Medicine 정보를 담을 DTO 리스트

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

        // prescriptionMedicines 리스트를 MedicineDto 리스트로 변환
        List<MedicineDto> medicineDtos = prescription.getPrescriptionMedicines().stream()
                .map(pm -> MedicineDto.builder()
                        .id(pm.getMedicine().getId()) // Medicine 엔티티의 ID
                        .medicineName(pm.getMedicine().getMedicineName())
                        .efficient(pm.getMedicine().getEfficient())
                        .useMethod(pm.getMedicine().getUseMethod())
                        .acquire(pm.getMedicine().getAcquire())
                        .warning(pm.getMedicine().getWarning())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .name(prescription.getName())
                .description(prescription.getDescription())
                .disease(diseaseDto)
                .medicines(medicineDtos)
                .build();
    }
}
