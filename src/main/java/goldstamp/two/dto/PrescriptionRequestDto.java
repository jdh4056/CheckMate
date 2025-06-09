// front + back/back/main/java/goldstamp/two/dto/PrescriptionRequestDto.java
package goldstamp.two.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime; // LocalTime 임포트 추가

@Data
public class PrescriptionRequestDto {
    private String diseaseName;
    private LocalDate prescriptionDate;
    private LocalTime alarmTimer1; // alarmTimer1 추가
    private LocalTime alarmTimer2; // alarmTimer2 추가
    private LocalTime alarmTimer3; // alarmTimer3 추가
    private LocalTime alarmTimer4; // alarmTimer4 추가
    private Integer numPerDay; // numPerDay 추가
    // 다른 필요한 필드 (예: description, medicines)도 추가할 수 있습니다.
}