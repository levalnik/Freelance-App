package org.levalnik.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.levalnik.enums.userEnum.UserRole;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHelper {
    private final JwtCreator jwtCreator;

    public UUID getUserId(String token) throws JwtException {
        return jwtCreator.getUserId(token);
    }

    public List<UserRole> getRoles(String token) throws JwtException {
        return jwtCreator.getRoles(token);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = jwtCreator.parseToken(token);
            Date expiration = claims.getBody().getExpiration();
            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            log.error("Expired token", e);
            throw e;
        } catch (JwtException e) {
            log.error("Invalid token", e);
            throw e;
        }
    }
}
