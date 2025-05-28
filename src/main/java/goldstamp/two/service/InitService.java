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
        Member member = createMember("Hwang", "Hwangw123", "goodboy", Gender.MAN, LocalDate.parse("2001-03-01"), 180, 70, new CurrentMed(LocalDate.parse("2025-03-01"), LocalDate.parse("2025-03-08")), new NextMed("감기", 7), MemberRole.USER);
        em.persist(member);

        List<Medicine> medicines = medicineRepository.findByEfficientContaining("감기");
        List<PrescriptionMedicine> pmList = new ArrayList<>();

        for (Medicine medicine : medicines) {
            PrescriptionMedicine pm = new PrescriptionMedicine();
            pm.setMedicine(medicine);
            pmList.add(pm);
        }
        Prescription prescription = Prescription.createPrescription(
                member,
                diseaseRepository.findByName("감기"),
                pmList.toArray(new PrescriptionMedicine[0])
        );

        em.persist(prescription);
    }
    public void dbInit2() {
        Member member = createMember("Lee","leeTiger88","fastpass", Gender.WOMAN, LocalDate.parse("1995-11-12"),165, 58, new CurrentMed(LocalDate.parse("2025-04-10"), LocalDate.parse("2025-04-17")), new NextMed("비염", 5), MemberRole.USER);
        em.persist(member);
        List<Medicine> medicines = medicineRepository.findByEfficientContaining("열상");
        List<PrescriptionMedicine> pmList = new ArrayList<>();

        for (Medicine medicine : medicines) {
            PrescriptionMedicine pm = new PrescriptionMedicine();
            pm.setMedicine(medicine);
            pmList.add(pm);
        }
        Prescription prescription = Prescription.createPrescription(
                member,
                diseaseRepository.findByName("열상"),
                pmList.toArray(new PrescriptionMedicine[0])
        );

        em.persist(prescription);
    }
    private Member createMember(String name, String loginId, String password, Gender gender, LocalDate birthDay, int height, int weight, CurrentMed currentMed, NextMed nextMed, MemberRole... roles) {
        Member member = new Member();
        member.setName(name);
        member.setLoginId(loginId);
        member.setPassword(passwordEncoder.encode(password));
        member.setGender(gender);
        member.setBirthDay(birthDay);
        member.setHeight(height);
        member.setWeight(weight);
        member.setCurrentMed(currentMed);
        member.setNextMed(nextMed);
        for (MemberRole role : roles) {
            member.addRole(role);
        }
        return member;
    }
}
