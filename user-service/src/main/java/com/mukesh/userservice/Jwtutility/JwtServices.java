package com.mukesh.userservice.Jwtutility;

import com.mukesh.userservice.Entity.user;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.Date;

@Service
public class JwtServices {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long Expiration;

    private SecretKey secretKey(){
        SecretKey key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return key;
    }
    public String GenerateToken(user user) {
        return Jwts.builder()
                .subject(user.getUseremail())
                .claim("role",user.getRole().name())
                .issuedAt(new Date())
                .signWith(secretKey())
                .setExpiration(new Date(System.currentTimeMillis() + Expiration))
                .compact();
    }
}
