package com.jensdev.common;

import java.io.Serializable;

record ResponseEnvelop(Object data, String message) implements Serializable {
}
