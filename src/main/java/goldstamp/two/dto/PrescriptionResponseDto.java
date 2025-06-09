// front + back/back/main/java/goldstamp/two/dto/PrescriptionResponseDto.java
package goldstamp.two.dto;
import goldstamp.two.domain.Disease;
import goldstamp.two.domain.Medicine;
import goldstamp.two.domain.PrescriptionMedicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalDate prescriptionDate;
    private LocalTime alarmTimer1;
    private LocalTime alarmTimer2;
    private LocalTime alarmTimer3;
    private LocalTime alarmTimer4;
    private Integer numPerDay; // numPerDay 추가
    private DiseaseDto disease;
    private List<PrescriptionMedicineResponseDto> medicines;

    public static PrescriptionResponseDto fromEntity(goldstamp.two.domain.Prescription prescription) {
        DiseaseDto diseaseDto = null;
        if (prescription.getDisease() != null) {
            diseaseDto = DiseaseDto.builder()
                    .id(prescription.getDisease().getId())
                    .name(prescription.getDisease().getName())
                    .explain(prescription.getDisease().getExplain())
                    .build();
        }

        List<PrescriptionMedicineResponseDto> prescriptionMedicineDtos = prescription.getPrescriptionMedicines().stream()
                .map(pm -> PrescriptionMedicineResponseDto.fromEntity(pm))
                .collect(Collectors.toList());

        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .name(prescription.getName())
                .description(prescription.getDescription())
                .prescriptionDate(prescription.getPrescriptionDate())
                .alarmTimer1(prescription.getAlarmTimer1())
                .alarmTimer2(prescription.getAlarmTimer2())
                .alarmTimer3(prescription.getAlarmTimer3())
                .alarmTimer4(prescription.getAlarmTimer4())
                .numPerDay(prescription.getNumPerDay()) // numPerDay 추가
                .disease(diseaseDto)
                .medicines(prescriptionMedicineDtos)
                .build();
    }
}