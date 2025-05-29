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

/**
 * Clase utilitaria para validar y extraer información de tokens JWT.
 */
@Component
public class JwtUtil {

    /**
     * Clave privada para la validación del token, inyectada desde configuración.
     */
    @Value("${spring.security.jwt.private.key}")
    private String privateKey;
    /**
     * Valida un token JWT verificando su firma y expiración.
     *
     * @param token el token JWT a validar
     * @return el objeto DecodedJWT si el token es válido y no expirado
     * @throws JWTVerificationException si el token es inválido o expiró
     */
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
    /**
     * Extrae el nombre de usuario (subject) del token decodificado.
     *
     * @param decodedJWT el token JWT decodificado
     * @return el nombre de usuario contenido en el token
     */
    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }
    /**
     * Obtiene un claim específico del token decodificado.
     *
     * @param decodedJWT el token JWT decodificado
     * @param claimName el nombre del claim a obtener
     * @return el claim solicitado
     */
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }
    /**
     * Devuelve todos los claims presentes en el token decodificado.
     *
     * @param decodedJWT el token JWT decodificado
     * @return un mapa con todos los claims
     */
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }
    /**
     * Extrae el rol (authorities) del token decodificado.
     *
     * @param decodedJWT el token JWT decodificado
     * @return el rol del usuario (ejemplo: "ROLE_USER")
     */
    public String extractRole(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("authorities").asString();
    }
    /**
     * Extrae el userId del token decodificado.
     *
     * @param decodedJWT el token JWT decodificado
     * @return el ID del usuario, si está presente en el token
     */
    public String extractUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("userId").asString();
    }
}
