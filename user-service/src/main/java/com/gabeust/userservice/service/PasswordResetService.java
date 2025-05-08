package com.gabeust.userservice.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabeust.userservice.entity.User;
import com.gabeust.userservice.repository.UserRepository;
import com.gabeust.userservice.util.JwtUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    public PasswordResetService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;

    }

    public String createResetToken(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with that email");
        }
        return jwtUtils.createPasswordResetToken(email);
    }

    public Boolean IsValidToken(String token){
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(token);
            return decodedJWT.getExpiresAt().after(new Date());
        }catch (JWTVerificationException e){
            return false; // Token no valido o expirado.
        }
    }

    public void resetPassword(String token, String newPassword) {
        if (!IsValidToken(token)) {
            throw new IllegalArgumentException("Token not valid or expired");
        }
        //Extrae el mail del token
        DecodedJWT decodedJWT = jwtUtils.validateToken(token);
        String email = decodedJWT.getSubject();
        //busca al usuario por email
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with that email");
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        // Reactiva la cuenta y credenciales
        user.setAccountNotLocked(true); // Desbloquear la cuenta
        user.setCredentialNotExpired(true); // Credenciales activas
        user.setFailedAttempts(0); // Reiniciar intentos fallidos

        userRepository.save(user);
    }
}
