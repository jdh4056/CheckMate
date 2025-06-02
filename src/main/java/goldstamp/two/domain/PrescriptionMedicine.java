package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore; // JsonIgnore 임포트 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "prescription_medicines")
public class PrescriptionMedicine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "prescription_medicine_id")
    private Long id;

    @JsonIgnore // Prescription과 PrescriptionMedicine은 양방향 관계이므로 한쪽에 JsonIgnore 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;
    // 필요 시 복용량, 복용 기간 등의 필드 추가 가능
}
