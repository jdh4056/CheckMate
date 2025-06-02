package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore; // JsonIgnore 임포트 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @JsonIgnore // Member와 Prescription은 양방향 관계이므로 한쪽에 JsonIgnore 추가
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

    public static Prescription createPrescription(Member member, Disease disease, PrescriptionMedicine... prescriptionMedicines) {
        Prescription prescription = new Prescription();
        prescription.setMember(member);
        prescription.setDisease(disease);
        for (PrescriptionMedicine prescriptionMedicine : prescriptionMedicines ) {
            prescription.addMedicine(prescriptionMedicine);
        }
        return prescription;
    }
}
