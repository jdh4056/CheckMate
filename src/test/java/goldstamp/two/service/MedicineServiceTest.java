package goldstamp.two.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class MedicineServiceTest {

    @Autowired MedicineService medicineService;

    @Test
    void saveDb() throws IOException {
        medicineService.saveMedicinesAll();
    }
}
