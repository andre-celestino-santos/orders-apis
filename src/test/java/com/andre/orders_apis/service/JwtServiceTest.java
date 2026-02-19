package com.andre.orders_apis.service;

import com.andre.orders_apis.entity.Role;
import com.andre.orders_apis.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class JwtServiceTest {

    private final String tokenSecretKey = "184faef3-b055-4921-b202-1c818da97026";
    private final Long tokenExpiration = 1L;

    private final JwtService jwtService = new JwtService(tokenSecretKey, tokenExpiration);

    @Test
    public void shouldGenerateTokenSuccessfully() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pass");

        Role roleOne = new Role();
        roleOne.setName("ROLE_ONE");

        Role roleTwo = new Role();
        roleTwo.setName("ROLE_TWO");

        Set<Role> roles = Set.of(roleOne, roleTwo);

        user.setRoles(roles);

        String generatedToken = jwtService.generateToken(user);

        Assertions.assertThat(generatedToken).isNotNull();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecretKey.getBytes()))
                .build()
                .parseClaimsJws(generatedToken);

        Claims claims = jwsClaims.getBody();

        Assertions.assertThat(claims.getSubject()).isEqualTo(user.getUsername());

        List<String> claimRoles = claims.get("roles", List.class);

        Assertions.assertThat(claimRoles).hasSize(2);

        long claimRolesCount = claimRoles.stream()
                .filter(claim -> claim.equals(roleOne.getName()) || claim.equals(roleTwo.getName()))
                .count();

        Assertions.assertThat(claimRolesCount).isEqualTo(2);

        Assertions.assertThat(claims.getIssuedAt()).isNotNull();
        Assertions.assertThat(claims.getExpiration()).isNotNull();

        Duration between = Duration.between(claims.getIssuedAt().toInstant(), claims.getExpiration().toInstant());

        Assertions.assertThat(between).hasHours(tokenExpiration);

        Assertions.assertThat(jwsClaims.getSignature()).isNotNull();

        Assertions.assertThat(jwsClaims.getHeader().getAlgorithm()).isEqualTo(SignatureAlgorithm.HS256.getValue());
    }

}