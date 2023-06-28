package com.jensdev.common.responseEnvelop;

import java.io.Serializable;

public record ResponseEnvelop(Object data, String message) implements Serializable {
}
