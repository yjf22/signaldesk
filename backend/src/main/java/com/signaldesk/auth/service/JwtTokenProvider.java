package com.signaldesk.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:86400000}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessExpiration;
        this.refreshTokenExpiration = refreshExpiration;
    }

    public String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, refreshTokenExpiration);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    private String generateToken(Long userId, String username, long expiration) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
