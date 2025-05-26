package goldstamp.two.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InitServiceTest {

    @Autowired
    InitService initService;

    @Test
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }
}
