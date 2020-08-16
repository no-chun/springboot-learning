package com.chun.springsecurityjwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    public static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public static String generateToken(String username, String authorities) {
        return Jwts.builder().claim("authorities", authorities)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }
}
