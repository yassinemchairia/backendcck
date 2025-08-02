package com.example.cckback;

import com.example.cckback.service.SurveillanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class CckbackApplicationTests {

    @MockBean
    private SurveillanceService surveillanceService; // Mock le service

    @Test
    void contextLoads() {
    }
}
