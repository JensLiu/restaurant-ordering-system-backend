package com.jensdev;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantOrderingSystemBackendApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServerEndpointExporter serverEndpointExporter; // Mocking WebSocket bean


    @Test
    void contextLoads() throws JsonProcessingException {
    }

}
