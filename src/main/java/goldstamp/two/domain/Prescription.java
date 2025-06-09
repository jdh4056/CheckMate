// front + back/back/main/java/goldstamp/two/domain/Prescription.java
package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "prescriptions")
public class Prescription {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "prescription_id")
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "disease_id")
    private Disease disease;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    private List<PrescriptionMedicine> prescriptionMedicines = new ArrayList<>();

    @Column(length = 1000)
    private String name;

    @Column(length = 3000)
    private String description;

    private LocalDate prescriptionDate;

    @Column(nullable = true)
    private LocalTime alarmTimer1;

    @Column(nullable = true)
    private LocalTime alarmTimer2;

    @Column(nullable = true)
    private LocalTime alarmTimer3;

    @Column(nullable = true)
    private LocalTime alarmTimer4;

    @Column(name = "num_per_day", nullable = true) // num_per_day 컬럼 추가
    private Integer numPerDay; // Integer 타입으로 매핑

    public void setMember(Member member) {
        this.member = member;
        member.getPrescriptions().add(this);
    }
    public void setDisease(Disease disease) {
        this.disease = disease;
        disease.setPrescription(this);
    }
    public void addMedicine(PrescriptionMedicine prescriptionMedicine) {
        prescriptionMedicines.add((prescriptionMedicine));
        prescriptionMedicine.setPrescription(this);
    }

    // prescriptionDate를 인자로 받도록 createPrescription 메서드 수정
    public static Prescription createPrescription(Member member, Disease disease, LocalDate prescriptionDate, PrescriptionMedicine... prescriptionMedicines) {
        Prescription prescription = new Prescription();
        prescription.setMember(member);
        prescription.setDisease(disease);
        for (PrescriptionMedicine prescriptionMedicine : prescriptionMedicines ) {
            prescription.addMedicine(prescriptionMedicine);
        }
        prescription.setPrescriptionDate(prescriptionDate);
        return prescription;
    }

    // 빈 처방전 생성 시 사용할 팩토리 메서드 (선택 사항)
    public static Prescription createEmptyPrescription(Member member, LocalDate prescriptionDate) {
        Prescription prescription = new Prescription();
        prescription.setMember(member);
        prescription.setName("미지정 질병");
        prescription.setDescription("설명 없음");
        prescription.setPrescriptionDate(prescriptionDate);
        return prescription;
    }
}