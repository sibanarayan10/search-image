package com.searchimage.search_image.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JWTUtility {

    private final Key key;
    private final long expiration;

    public JWTUtility() {
        String secret = System.getenv("JWT_SECRET");
        String exp = System.getenv("JWT_EXPIRATION");

        if (secret == null || exp == null) {
            throw new IllegalStateException("JWT environment variables not set");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = Long.parseLong(exp);
    }

    // Generate token
    public String generateToken(String email, Long userId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Extract userId
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // Validate token
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Internal helpers
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private Claims getClaims(String token) {
        return parseToken(token).getBody();
    }
}

