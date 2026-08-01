package com.mukesh.apigateway.Configuration;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    public boolean ValidateToken(String token) {
        if(isTokenExpired(token)){
            return false;
        }
        return true;
    }

    private SecretKey getKey() {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        return secretKey;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .getExpiration();


        if(expiration.before(new Date())){
            return true;
        }
        return false;
    }


    public String ExtractEmailFromToken(String token) {
        String email = null;
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        email = claims.getSubject();
        return email;
    }


    public String ExtractRole(String token) {
        String role = null;
        Claims claims = Jwts.parser().verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


        role = claims.get("role").toString();
        return role;
    }
}
