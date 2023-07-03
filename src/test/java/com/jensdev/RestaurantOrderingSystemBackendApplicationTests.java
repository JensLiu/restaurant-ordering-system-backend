package com.jensdev;

import com.jensdev.analysis.repository.OrderAnalysisRepository;
import com.jensdev.analysis.service.OrderAnalysisServiceImpl;
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

    @Autowired
    OrderAnalysisServiceImpl orderAnalysisService;

    @Autowired
    OrderAnalysisRepository orderAnalysisRepository;

    @Test
    void contextLoads() {
//        orderAnalysisRepository.getOrderProcessTime().forEach(System.out::println);
//        System.out.println("trending categories");
//        orderAnalysisService.trendingCategories().forEach(System.out::println);
//        orderAnalysisService.valuableCustomers().forEach(System.out::println);
//        orderAnalysisService.peakHours().forEach(System.out::println);
        orderAnalysisRepository.getTrendingMenuItems().forEach(System.out::println);
    }

}
