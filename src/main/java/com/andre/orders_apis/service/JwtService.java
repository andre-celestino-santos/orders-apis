package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtService {

    private final String tokenSecretKey;
    private final Long tokenExpiration;

    public JwtService(@Value("${orders-apis.token.secret-key:184faef3-b055-4921-b202-1c818da97026}") String tokenSecretKey,
                      @Value("${orders-apis.token.expiration:1}") Long tokenExpiration) {
        this.tokenSecretKey = tokenSecretKey;
        this.tokenExpiration = tokenExpiration;
    }

    public String generateToken(User user) {
        long expiration = ( 1000 * 60 ) * ( tokenExpiration * 60 );

        Date issuedAt = new Date();

        Date expirationDate = new Date(issuedAt.getTime() + expiration);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(tokenSecretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecretKey.getBytes()))
                .build()
                .parseClaimsJws(token);
    }

}