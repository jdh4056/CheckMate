package goldstamp.two.controller;

import goldstamp.two.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members/{memberId}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Long> createPrescription(
            @PathVariable Long memberId,
            @RequestParam String diseaseName
    ) {
        Long prescriptionId = prescriptionService.createPrescriptionByDiseaseName(memberId, diseaseName);
        return ResponseEntity.ok(prescriptionId);
    }
}