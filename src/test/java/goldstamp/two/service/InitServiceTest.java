package goldstamp.two.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class InitServiceTest {

    @Autowired
    InitService initService;

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_FIRST_TIME_TEST", matches = "true")
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }
}
