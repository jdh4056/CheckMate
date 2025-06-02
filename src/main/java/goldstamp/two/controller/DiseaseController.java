package goldstamp.two.controller;

import goldstamp.two.domain.Disease;
import goldstamp.two.dto.DiseaseDto; // DiseaseDto 클래스 임포트
import goldstamp.two.service.DiseaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // Collectors 클래스 임포트

@RestController
@RequestMapping("/diseases")
@RequiredArgsConstructor
public class DiseaseController {

    private final DiseaseService diseaseService;

    @GetMapping
    public ResponseEntity<List<DiseaseDto>> autocompleteDiseases(@RequestParam("query") String query) {
        List<Disease> diseases = diseaseService.searchDiseases(query);
        // 질병 객체 리스트를 DiseaseDto 리스트로 변환하여 반환
        List<DiseaseDto> diseaseDtos = diseases.stream()
                .map(disease -> DiseaseDto.builder()
                        .id(disease.getId())
                        .name(disease.getName())
                        .explain(disease.getExplain())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(diseaseDtos); // diseaseNames 대신 diseaseDtos 사용
    }
}