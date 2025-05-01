package com.gabeust.userservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.gabeust.userservice.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtils {

    @Value("${spring.security.jwt.private.key}")
    private String privateKey;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtUtils(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        String email = authentication.getName();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return JWT.create().withSubject(email)
                .withClaim("authorities", authorities).withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 18000000)).withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis())).sign(algorithm);
    }

    public String createPasswordResetToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 900000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) {
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            throw new JWTVerificationException("Invalid or expired token");
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // Verificar manualmente si el token ha expirado
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new JWTVerificationException("expired token");
            }

            return decodedJWT;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(" Invalid token, Unauthorized");
        }

    }

    public void invalidateToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        long expiresIn = decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklistToken(token, expiresIn);
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaim(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
}
