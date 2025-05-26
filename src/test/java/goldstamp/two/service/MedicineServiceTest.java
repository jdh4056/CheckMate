package goldstamp.two.service;

import goldstamp.two.domain.Medicine;
import goldstamp.two.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
public class MedicineServiceTest {

    @Autowired MedicineService medicineService;

    @Test
    void saveDb() throws IOException {
        medicineService.saveMedicinesAll();
    }
}
