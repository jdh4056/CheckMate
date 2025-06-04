package goldstamp.two.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PrescriptionMedicineRequestDto {
    private Long medicineId; // 약 ID (기존 약을 참조할 경우)
    private String medicineName; // 약 이름 (새로운 약을 추가하거나 검색할 경우)
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numPerDay; // int 대신 Integer로 변경
    private LocalTime alarmTimer1;
    private LocalTime alarmTimer2;
    private LocalTime alarmTimer3;
    private LocalTime alarmTimer4;
    private Integer dose; // 복용량 숫자 추가
    private String doseType; // 복용량 타입 추가
}
