package com.maximorero.k8_auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
class K8AuthApplicationTests {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
    }
}