package goldstamp.two.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class InitServiceTest {

    @Autowired
    InitService initService;

    @Test
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }
}
