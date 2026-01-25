package com.ecw.ccdadeidtool;

import com.ecw.deidtool.DeidToolApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = DeidToolApplication.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
@ActiveProfiles("test")
class CcdaDeidToolApplicationTests {

    @Test
    void contextLoads() {
    }

}
