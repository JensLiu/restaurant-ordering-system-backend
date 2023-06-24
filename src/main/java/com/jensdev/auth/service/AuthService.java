package com.jensdev.auth.service;

import com.jensdev.user.modal.Role;
import com.jensdev.user.modal.User;
import com.jensdev.user.repository.UserRepository;
import com.jensdev.auth.model.AuthRequest;
import com.jensdev.auth.model.AuthResponse;
import com.jensdev.auth.model.RefreshResponse;
import com.jensdev.auth.model.RegisterRequest;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .registeredAt(new Date())
                .build();

        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .id(savedUser.getId())
                .role(savedUser.getRole().name())
                .firstname(savedUser.getFirstname())
                .lastname(savedUser.getLastname())
                .imageSrc(savedUser.getImageSrc())
                .build();
    }

    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .id(user.getId())
                .role(user.getRole().name())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .imageSrc(user.getImageSrc())
                .build();
    }

    public RefreshResponse refreshToken(String jwtToken) throws ExpiredJwtException {
        final String userEmail = jwtService.extractUsername(jwtToken);
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return RefreshResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }


}
