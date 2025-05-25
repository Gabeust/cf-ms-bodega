package com.gabeust.apigateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;


@Component
public class JwtUtil {

    @Value("${spring.security.jwt.private.key}")
    private String privateKey;

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new JWTVerificationException("Token expirado");
            }

            return decodedJWT;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token inválido o no autorizado");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
    public String extractRole(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("authorities").asString(); // puede venir como "ROLE_USER"
    }

    public String extractUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("userId").asString(); // asegúrate de incluirlo en el token si lo necesitas
    }
}
