package org.levalnik.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.levalnik.config.JwtProperties;
import org.levalnik.enums.userEnum.UserRole;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtCreator {
    private static JwtProperties jwtProperties;
    private static Key hmacKey;

    public JwtCreator(JwtProperties jwtProperties) {
        JwtCreator.jwtProperties = jwtProperties;
    }
    @PostConstruct
    protected void init() {
        String secret = jwtProperties.getSecret();
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        hmacKey = new SecretKeySpec(decodedKey, "HmacSHA256");    }

    public static String createToken(UUID userId, List<UserRole> roles) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(jwtProperties.getExpirationMillis())))
                .signWith(hmacKey)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parser().setSigningKey(hmacKey).build().parseClaimsJws(token);
    }

    public UUID getUserId(String token) {
        return parseToken(token).getBody().get("userId", UUID.class);
    }

    public List<UserRole> getRoles(String token) {
        List<String> roleStr =  parseToken(token).getBody().get("roles", List.class);
        return roleStr.stream().map(UserRole::valueOf).collect(Collectors.toList());
    }
}
