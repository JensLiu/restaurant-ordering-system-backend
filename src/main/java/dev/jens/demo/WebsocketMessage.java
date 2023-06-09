package dev.jens.demo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebsocketMessage {
    private String from;
    private String to;
}
