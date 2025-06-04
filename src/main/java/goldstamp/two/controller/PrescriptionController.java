package goldstamp.two.controller;

import goldstamp.two.domain.Prescription;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.dto.PrescriptionRequestDto;
import goldstamp.two.dto.PrescriptionMedicineRequestDto; // PrescriptionMedicineRequestDto 임포트 추가
import goldstamp.two.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/members/{memberId}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<Long> createPrescription(
            @PathVariable Long memberId,
            @RequestBody PrescriptionRequestDto requestDto
    ) {
        Long prescriptionId = prescriptionService.createPrescriptionByDiseaseName(memberId, requestDto);
        return ResponseEntity.ok(prescriptionId);
    }

    @PostMapping("/empty")
    public ResponseEntity<Long> createEmptyPrescription(
            @PathVariable Long memberId,
            @RequestParam("prescriptionDate") LocalDate prescriptionDate
    ) {
        Long prescriptionId = prescriptionService.createEmptyPrescription(memberId, prescriptionDate);
        return ResponseEntity.ok(prescriptionId);
    }

    @GetMapping("/{prescriptionId}/edit")
    public ResponseEntity<Prescription> getPrescriptionDetails(
            @PathVariable Long memberId,
            @PathVariable Long prescriptionId
    ) {
        Prescription prescription = prescriptionService.findOnePrescription(prescriptionId);
        return ResponseEntity.ok(prescription);
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getPrescriptionsByMemberId(@PathVariable Long memberId) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByMemberId(memberId);
        return ResponseEntity.ok(prescriptions);
    }

    // 질병 이름으로 처방전 조회 API 추가
    @GetMapping("/byDiseaseName")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDiseaseName(
            @PathVariable Long memberId,
            @RequestParam("diseaseName") String diseaseName) {
        List<Prescription> prescriptions = prescriptionService.findPrescriptionsByDiseaseName(memberId, diseaseName);
        return ResponseEntity.ok(prescriptions);
    }

    // 질병명으로 처방전을 찾거나 생성하는 API 추가
    @PostMapping("/findOrCreateByDiseaseName")
    public ResponseEntity<Long> findOrCreatePrescriptionByDiseaseName(
            @PathVariable Long memberId,
            @RequestParam("diseaseName") String diseaseName,
            @RequestParam("prescriptionDate") LocalDate prescriptionDate
    ) {
        Long prescriptionId = prescriptionService.findOrCreatePrescriptionForDisease(memberId, diseaseName, prescriptionDate);
        return ResponseEntity.ok(prescriptionId);
    }

    @PatchMapping("/{prescriptionId}/disease")
    public ResponseEntity<Void> addDiseaseToPrescription(
            @PathVariable Long memberId,
            @PathVariable Long prescriptionId,
            @RequestParam String diseaseName
    ) {
        prescriptionService.addDiseaseToPrescription(memberId, prescriptionId, diseaseName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{prescriptionId}/medicine")
    public ResponseEntity<Void> addMedicineToPrescription(
            @PathVariable Long memberId,
            @PathVariable Long prescriptionId,
            @RequestBody PrescriptionMedicineRequestDto requestDto // DTO로 변경
    ) {
        prescriptionService.addMedicineToPrescription(memberId, prescriptionId, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<String> deletePrescription(
            @PathVariable("memberId") Long memberIdFromPath,
            @PathVariable("prescriptionId") Long prescriptionIdToDelete,
            @AuthenticationPrincipal MemberDto currentUser
    ) {
        if (!currentUser.getId().equals(memberIdFromPath)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete prescriptions for this member.");
        }
        try {
            prescriptionService.deletePrescription(prescriptionIdToDelete, memberIdFromPath);
            return ResponseEntity.ok("Prescription with ID " + prescriptionIdToDelete + " has been deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during prescription deletion.");
        }
    }
}
