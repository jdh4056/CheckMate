package goldstamp.two.controller;

import goldstamp.two.domain.Medicine;
import goldstamp.two.dto.MedicineDto;
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
    public ResponseEntity<List<MedicineDto>> searchMedicines(@RequestParam("query") String query) { // Method name changed for clarity, return type changed
        List<Medicine> medicines = medicineService.searchMedicines(query);
        // 약 객체 리스트에서 MedicineDto로 변환하여 반환
        List<MedicineDto> medicineDtos = medicines.stream()
                .map(medicine -> MedicineDto.builder()
                        .id(medicine.getId())
                        .medicineName(medicine.getMedicineName())
                        .efficient(medicine.getEfficient())
                        .useMethod(medicine.getUseMethod())
                        .acquire(medicine.getAcquire())
                        .warning(medicine.getWarning())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(medicineDtos);
    }
}

