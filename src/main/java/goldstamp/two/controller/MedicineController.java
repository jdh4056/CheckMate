package goldstamp.two.controller;

import goldstamp.two.domain.Medicine;
import goldstamp.two.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medicines") // 약 관련 API의 기본 경로
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<String>> autocompleteMedicines(@RequestParam("query") String query) {
        List<Medicine> medicines = medicineService.searchMedicines(query);
        // 약 객체 리스트에서 이름(medicineName)만 추출하여 반환
        List<String> medicineNames = medicines.stream()
                .map(Medicine::getMedicineName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicineNames);
    }
}
