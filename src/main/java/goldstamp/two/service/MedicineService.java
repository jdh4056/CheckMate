package goldstamp.two.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import goldstamp.two.domain.Medicine;
import goldstamp.two.dto.MedicineDto;
import goldstamp.two.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    public List<Medicine> searchMedicines(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(); // 빈 문자열이나 null이 오면 빈 리스트 반환
        }
        return medicineRepository.findByMedicineNameContainingIgnoreCase(keyword); //
    }

    public void saveMedicinesAll() throws IOException {
        Integer pageNo = 1;
        while (true) {
            boolean hasData = saveMedicines(pageNo.toString());
            if (!hasData) {
                System.out.println("데이터가 더 이상 없습니다. 종료합니다.");
                break;
            }
            pageNo++;
        }
    }
    private boolean saveMedicines(String pageNo) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=1fHdR6f1huRYBOpLQ5geHT9L2R4tiM5M2daOUlM4BzNfTBSRa2nd%2B%2BW7s5x3G3i73DR%2ByNPB%2BOBQNrnnPluJbQ%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("50", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("pageNo = " + pageNo);

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sb.toString());
        JsonNode items = root.path("body").path("items");

        // 결과 없을 경우 반복 종료
        if (items.isMissingNode() || !items.elements().hasNext()) {
            return false; // 반복문 종료 신호
        }

        List<MedicineDto> medicineDtoList = new ArrayList<>();
        for (JsonNode itemNode : items) {
            MedicineDto dto = new MedicineDto();
            dto.setMedicineName(itemNode.path("itemName").asText());
            dto.setEfficient(itemNode.path("efcyQesitm").asText());
            dto.setUseMethod(itemNode.path("useMethodQesitm").asText());
            dto.setAcquire(itemNode.path("atpnWarnQesitm").asText());
            dto.setWarning(itemNode.path("atpnQesitm").asText());
            medicineDtoList.add(dto);
        }

        if (medicineDtoList.isEmpty()) {
            return false; // 안전하게 종료
        }

        List<Medicine> medicines = new ArrayList<>();
        for (MedicineDto dto : medicineDtoList) {
            Medicine medicine = new Medicine();
            medicine.setMedicineName(dto.getMedicineName());
            medicine.setEfficient(dto.getEfficient());
            medicine.setUseMethod(dto.getUseMethod());
            medicine.setWarning(dto.getWarning());
            medicine.setAcquire(dto.getAcquire());
            medicines.add(medicine);
        }

        medicineRepository.saveAll(medicines);
        return true;
    }
}
