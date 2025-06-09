// front + back/back/main/java/goldstamp/two/dto/PrescriptionMedicineResponseDto.java
package goldstamp.two.dto;

import goldstamp.two.domain.PrescriptionMedicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionMedicineResponseDto {
    private Long id;
    private MedicineDto medicine; // 약 정보
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numPerDay; // int 대신 Integer로 변경
    private Integer totalDrugNum; // int 대신 Integer로 변경

    // PrescriptionMedicine 엔티티를 DTO로 변환하는 팩토리 메서드
    public static PrescriptionMedicineResponseDto fromEntity(PrescriptionMedicine pm) {
        return PrescriptionMedicineResponseDto.builder()
                .id(pm.getId())
                .medicine(MedicineDto.builder()
                        .id(pm.getMedicine().getId())
                        .medicineName(pm.getMedicine().getMedicineName())
                        .efficient(pm.getMedicine().getEfficient())
                        .useMethod(pm.getMedicine().getUseMethod())
                        .acquire(pm.getMedicine().getAcquire())
                        .warning(pm.getMedicine().getWarning())
                        .build())
                .startDate(pm.getStartDate())
                .endDate(pm.getEndDate())
                .numPerDay(pm.getNumPerDay())
                .totalDrugNum(pm.getTotalDrugNum())
                .build();
    }
}