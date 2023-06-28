package com.jensdev.notification.service;

import com.jensdev.user.modal.User;
import jakarta.websocket.Session;
import org.springframework.data.util.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserConnectionContext {
    // online chefs
    public static final Map<Long, SessionStatus> chefConnections
            = Collections.synchronizedMap(new HashMap<>());

    // online customers
    public static final Map<Long, SessionStatus> customerConnections
            = Collections.synchronizedMap(new HashMap<>());

    // online managers
    public static final Map<Long, SessionStatus> managerConnections
            = Collections.synchronizedMap(new HashMap<>());

}
