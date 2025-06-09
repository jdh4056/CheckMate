// front + back/back/main/java/goldstamp/two/service/InitService.java
package goldstamp.two.service;

import goldstamp.two.domain.*;
import goldstamp.two.repository.DiseaseRepository;
import goldstamp.two.repository.MedicineRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class InitService {

    private final EntityManager em;

    private final DiseaseRepository diseaseRepository;

    private final MedicineRepository medicineRepository;

    private final PasswordEncoder passwordEncoder;


    public void dbInit1() {
        Member member = createMember("Hwang", "Hwangw123", "goodboy", Gender.MAN, LocalDate.parse("2001-03-01"), 180, 70, MemberRole.USER);
        em.persist(member);

        List<Medicine> medicines = medicineRepository.findByEfficientContaining("감기");
        List<PrescriptionMedicine> pmList = new ArrayList<>();

        for (Medicine medicine : medicines) {
            PrescriptionMedicine pm = new PrescriptionMedicine();
            pm.setMedicine(medicine);
            pm.setStartDate(LocalDate.of(2025, 6, 1));
            pm.setEndDate(LocalDate.of(2025, 6, 7));
            pm.setNumPerDay(3);
            pm.setDose(1);
            pm.setDoseType("정");
            pm.calculateTotalDrugNum();
            pmList.add(pm);
        }
        Prescription prescription = Prescription.createPrescription(
                member,
                diseaseRepository.findByName("감기"),
                LocalDate.now(),
                pmList.toArray(new PrescriptionMedicine[0])
        );
        prescription.setAlarmTimer1(LocalTime.of(9,0));
        prescription.setAlarmTimer2(LocalTime.of(13,0));
        prescription.setAlarmTimer3(LocalTime.of(18,0));
        prescription.setAlarmTimer4(null);
        prescription.setNumPerDay(3); // numPerDay 추가

        em.persist(prescription);
    }
    public void dbInit2() {
        Member member = createMember("Lee","leeTiger88","fastpass", Gender.WOMAN, LocalDate.parse("1995-11-12"),165, 58, MemberRole.USER);
        em.persist(member);
        List<Medicine> medicines = medicineRepository.findByEfficientContaining("열상");
        List<PrescriptionMedicine> pmList = new ArrayList<>();

        for (Medicine medicine : medicines) {
            PrescriptionMedicine pm = new PrescriptionMedicine();
            pm.setMedicine(medicine);
            pm.setStartDate(LocalDate.of(2025, 5, 15));
            pm.setEndDate(LocalDate.of(2025, 5, 20));
            pm.setNumPerDay(2);
            pm.setDose(5);
            pm.setDoseType("ml");
            pm.calculateTotalDrugNum();
            pmList.add(pm);
        }
        Prescription prescription = Prescription.createPrescription(
                member,
                diseaseRepository.findByName("열상"),
                LocalDate.now(),
                pmList.toArray(new PrescriptionMedicine[0])
        );
        prescription.setAlarmTimer1(LocalTime.of(10,0));
        prescription.setAlarmTimer2(LocalTime.of(16,0));
        prescription.setAlarmTimer3(null);
        prescription.setAlarmTimer4(null);
        prescription.setNumPerDay(2); // numPerDay 추가

        em.persist(prescription);
    }
    private Member createMember(String name, String loginId, String password, Gender gender, LocalDate birthDay, int height, int weight, MemberRole... roles) {
        Member member = new Member();
        member.setName(name);
        member.setLoginId(loginId);
        member.setPassword(passwordEncoder.encode(password));
        member.setGender(gender);
        member.setBirthDay(birthDay);
        member.setHeight(height);
        member.setWeight(weight);
        for (MemberRole role : roles) {
            member.addRole(role);
        }
        return member;
    }
}