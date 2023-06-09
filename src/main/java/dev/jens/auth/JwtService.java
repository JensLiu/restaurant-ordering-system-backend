package dev.jens.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.access-token.cookie-name}")
    private String accessTokenCookieName;

    @Value("${app.jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;

    @Value("${app.jwt.access-token.expiration-ms}")
    private Long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenExpirationMs;

    public String extractUsername(String token) throws ExpiredJwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails, new Date(System.currentTimeMillis() + accessTokenExpirationMs));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails, new Date(System.currentTimeMillis() + refreshTokenExpirationMs));
    }

    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails, Date expireDate) {
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())  // username in the UserDetails is the email
                .setIssuedAt(new Date(System.currentTimeMillis()))  // when is the token created, helps the client to check if the token has expired
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        // check if this token belongs to the user and if the token has expired
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        // if the token has expired
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        List<Cookie> filteredCookies = Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .toList();
        if (filteredCookies.size() >= 1) {
            return filteredCookies.get(0).getValue();
        }
        return null;
    }

    public String getAccessTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring("Bearer ".length());
    }

    public String getAccessToken(HttpServletRequest request) {
        String cookie = getAccessTokenFromRequest(request);
        if (cookie == null) {
            return getTokenFromCookies(request, accessTokenCookieName);
        }
        return cookie;
    }

    public String getRefreshToken(HttpServletRequest request) {
        return getTokenFromCookies(request, refreshTokenCookieName);
    }

    public ResponseCookie getAccessTokenCookie(String token) {
        return ResponseCookie
                .from(accessTokenCookieName)
                .value(token)
                .httpOnly(true)
                .path("/")
                .maxAge(accessTokenExpirationMs)
                .build();
    }

    public ResponseCookie getRefreshTokenCookie(String token) {
        return ResponseCookie
                .from(refreshTokenCookieName)
                .value(token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpirationMs)
                .build();
    }

}
