package com.jensdev.auth.service;

import com.jensdev.auth.model.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtService.getAccessTokenCookie(response.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, jwtService.getRefreshTokenCookie(response.getRefreshToken()).toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtService.getAccessTokenCookie(response.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, jwtService.getRefreshTokenCookie(response.getRefreshToken()).toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(HttpServletRequest request) {
        log.info("Refreshing token");
        try {
            String refreshToken = jwtService.getRefreshToken(request);
            RefreshResponse response = authService.refreshToken(refreshToken);
            log.debug("Token refreshed");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtService.getAccessTokenCookie(response.getAccessToken()).toString())
                    .header(HttpHeaders.SET_COOKIE, jwtService.getRefreshTokenCookie(response.getRefreshToken()).toString())
                    .body(response);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

//    @PostMapping("/refresh")
//     public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
//        RefreshResponse response = authService.refreshToken(request.getRefreshToken());
//            log.debug("Token refreshed");
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.SET_COOKIE, jwtService.getAccessTokenCookie(response.getAccessToken()).toString())
//                    .header(HttpHeaders.SET_COOKIE, jwtService.getRefreshTokenCookie(response.getRefreshToken()).toString())
//                    .body(response);
//    }


}
