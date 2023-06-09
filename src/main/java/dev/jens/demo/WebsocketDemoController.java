package dev.jens.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WebsocketDemoController {
    @MessageMapping("notification")
    @SendTo("/topic/")
    public WebsocketMessage hello(WebsocketMessage message) {
        return WebsocketMessage.builder().from("server").to("client").build();
    }
}
