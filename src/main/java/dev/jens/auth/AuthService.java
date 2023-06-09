package dev.jens.auth;

import dev.jens.auth.models.AuthRequest;
import dev.jens.auth.models.AuthResponse;
import dev.jens.auth.models.RefreshResponse;
import dev.jens.auth.models.RegisterRequest;
import dev.jens.user.Role;
import dev.jens.user.User;
import dev.jens.user.UserRepository;
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
