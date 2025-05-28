package goldstamp.two.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiseaseServiceTest {
    @Autowired DiseaseService diseaseService;

    @Test
    void saveDb() throws Exception {
        diseaseService.saveDiseases();
    }
}
