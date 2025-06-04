package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@Table(name = "prescription_medicines")
public class PrescriptionMedicine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "prescription_medicine_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    // 새롭게 추가된 칼럼들 - nullable = true 추가
    @Column(nullable = true) // NULL 허용
    private LocalDate startDate; // 복용 시작 날짜

    @Column(nullable = true) // NULL 허용
    private LocalDate endDate; // 복용 종료 날짜

    @Column(nullable = true) // int 대신 Integer로 변경하여 NULL 허용
    private Integer numPerDay; // 하루 복용 횟수

    @Column(nullable = true) // int 대신 Integer로 변경하여 NULL 허용
    private Integer totalDrugNum; // 총 복용량 (계산 필드)

    @Column(nullable = true) // NULL 허용
    private LocalTime alarmTimer1; // 알람 시간 1

    @Column(nullable = true) // NULL 허용
    private LocalTime alarmTimer2; // 알람 시간 2

    @Column(nullable = true) // NULL 허용
    private LocalTime alarmTimer3; // 알람 시간 3

    @Column(nullable = true) // NULL 허용
    private LocalTime alarmTimer4; // 알람 시간 4

    // 새롭게 추가된 필드: 용량 및 용량 타입
    @Column(nullable = true) // NULL 허용
    private Integer dose; // 복용량 숫자 (예: 1, 50)

    @Column(length = 50, nullable = true) // NULL 허용, 길이 제한
    private String doseType; // 복용량 타입 (예: '정', 'ml', '가루약')

    /**
     * totalDrugNum 계산 메서드
     * startDate, endDate, numPerDay가 모두 null이 아닐 때만 계산합니다.
     */
    public void calculateTotalDrugNum() {
        if (startDate != null && endDate != null && numPerDay != null) {
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1; // 복용 기간 (시작일 포함)
            this.totalDrugNum = (int) daysBetween * numPerDay;
        } else {
            this.totalDrugNum = 0; // 날짜나 횟수가 없으면 0으로 설정
        }
    }

    /**
     * Setter에서 totalDrugNum 계산을 트리거하도록 수정
     * 각 필드가 설정될 때마다 총 복용량을 다시 계산합니다.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateTotalDrugNum(); // 날짜 변경 시 총 복용량 재계산
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        calculateTotalDrugNum(); // 날짜 변경 시 총 복용량 재계산
    }

    public void setNumPerDay(Integer numPerDay) {
        this.numPerDay = numPerDay;
        calculateTotalDrugNum(); // 복용 횟수 변경 시 총 복용량 재계산
    }
}
