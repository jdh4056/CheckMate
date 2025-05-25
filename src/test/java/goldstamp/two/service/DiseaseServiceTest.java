package goldstamp.two.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@SpringBootTest
public class DiseaseServiceTest {
    @Autowired DiseaseService diseaseService;

    @Test
    void saveDB() throws Exception {
        diseaseService.saveDiseases();
    }
}
