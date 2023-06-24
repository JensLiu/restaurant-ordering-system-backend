package com.jensdev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jensdev.notification.dto.BaseNotificationDto;
import com.jensdev.notification.dto.MessageNotificationDto;
import com.jensdev.notification.dto.OrderNotificationDto;
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
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"type\":\"ORDER\",\"orderId\":\"1\", \"orderStatus\":\"READY\"}";
        BaseNotificationDto notificationDto = BaseNotificationDto.fromJson(json);
        if (notificationDto instanceof MessageNotificationDto) {
            System.out.println(notificationDto);
            MessageNotificationDto messageNotificationDto = (MessageNotificationDto) notificationDto;
            System.out.println(messageNotificationDto);
        } else if (notificationDto instanceof OrderNotificationDto) {
            System.out.println(notificationDto);
            OrderNotificationDto orderNotificationDto = (OrderNotificationDto) notificationDto;
            System.out.println(orderNotificationDto);
        }
    }

}
