package com.example.milkdelivery.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // -----------------------
    // Generate Tokens
    // -----------------------
    public String generateAccessToken(String subject) {
        return buildToken(subject, accessExpirationMs);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(subject, refreshExpirationMs);
    }

    private String buildToken(String subject, long expiryMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // -----------------------
    // Extract info safely
    // -----------------------
    public String extractUsername(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException ex) {
            // ✅ Still return subject even if token expired
            return ex.getClaims().getSubject();
        }
    }

    // -----------------------
    // Validate tokens
    // -----------------------
    public boolean validateAccessToken(String token, String username) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException ex) {
            // Token expired → not valid anymore
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException ex) {
            // refresh token also expired
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // -----------------------
    // Internal parse method with clock skew handling
    // -----------------------
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60) // allow 1 min clock skew
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // -----------------------
    // Helper for graceful expired check
    // -----------------------
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
}
