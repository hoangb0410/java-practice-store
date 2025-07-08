package com.store.store.common.jwt;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.store.model.Store;
import com.store.store.model.User;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final Algorithm algorithm;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    public String generateAccessToken(Object principal) {
        return generateToken(principal, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Object principal) {
        return generateToken(principal, jwtProperties.getRefreshTokenExpiration());
    }

    private String generateToken(Object principal, long expirationSeconds) {
        JWTCreator.Builder builder = JWT.create();
        if (principal instanceof User user) {
            builder.withSubject("user:" + user.getId())
                    .withClaim("email", user.getEmail())
                    .withClaim("isAdmin", user.getIsAdmin());
        } else if (principal instanceof Store store) {
            builder.withSubject("store:" + store.getId())
                    .withClaim("email", store.getEmail())
                    .withClaim("isApproved", store.getIsApproved());
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }

        return builder
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String extractSubject(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            String subject = decodedJWT.getSubject();
            if (subject == null || !subject.contains(":")) {
                throw new RuntimeException("Invalid subject format");
            }
            return subject;
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public boolean isTokenValid(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
