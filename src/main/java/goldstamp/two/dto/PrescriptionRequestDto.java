package goldstamp.two.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PrescriptionRequestDto {
    private String diseaseName;
    private LocalDate prescriptionDate; // 처방 날짜 필드 추가
    // 다른 필요한 필드 (예: description, medicines)도 추가할 수 있습니다.
}