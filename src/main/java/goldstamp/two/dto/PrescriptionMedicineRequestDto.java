// front + back/back/main/java/goldstamp/two/dto/PrescriptionMedicineRequestDto.java
package goldstamp.two.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PrescriptionMedicineRequestDto {
    private Long medicineId; // 약 ID (기존 약을 참조할 경우)
    private String medicineName; // 약 이름 (새로운 약을 추가하거나 검색할 경우)
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numPerDay; // int 대신 Integer로 변경
    private Integer dose; // 복용량 숫자 추가
    private String doseType; // 복용량 타입 추가
}