package com.jensdev.notification.controller;

import com.jensdev.auth.service.JwtService;
import com.jensdev.common.exceptions.AuthException;
import com.jensdev.notification.service.UserConnectionContext;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/ws-connect")
@RequiredArgsConstructor
public class WsTokenController {

    private final UserService userService;
    private final JwtService jwtService;
    record WsTokenResponse(String token){}

    @GetMapping
    public ResponseEntity<WsTokenResponse> getWsToken(Authentication authentication) {
        User user = userService.getUser(authentication);
//        UserConnectionContext
        return ResponseEntity.ok(new WsTokenResponse("token"));
    }
}
