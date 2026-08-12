package com.rentro.util;

import com.rentro.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    private final long accessTokenExpiryMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secretKey,
            @Value("${app.jwt.access-token-expiry-ms}") long expMs){
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMs = expMs;
    }

    public String generateAccessToken(UserEntity user){
        UserEntity.Role role = user.getRole();
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(
                        Map.of(
                                "email",user.getEmail(),
                                "role",role.name(),
                                "name",user.getFullName()
                        )
                )
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessTokenExpiryMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token){
        return extractAllClaims(token).getSubject();
    }

    public boolean isValid(String token){
        try{
            extractAllClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public long getAccessTokenExpiryMs(){
        return accessTokenExpiryMs;
    }
}
