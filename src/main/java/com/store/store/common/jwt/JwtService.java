package com.store.store.common.jwt;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.store.model.User;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(User user, long expirationSeconds) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("isAdmin", user.getIsAdmin())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
