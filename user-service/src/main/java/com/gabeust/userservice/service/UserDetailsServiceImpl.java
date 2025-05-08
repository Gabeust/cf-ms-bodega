package com.gabeust.userservice.service;

import com.gabeust.userservice.dto.AuthLoginRequestDTO;
import com.gabeust.userservice.dto.AuthResponseDTO;
import com.gabeust.userservice.entity.User;
import com.gabeust.userservice.repository.UserRepository;
import com.gabeust.userservice.util.JwtUtils;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("User with email " + email + " was not found.");
        }
        if (!user.getAccountNotLocked()) {
            throw new LockedException("The account is locked due to multiple failed login attempts.");
        }
        List<SimpleGrantedAuthority> authorityList = user.getRolesList().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnable(),
                user.getAccountNotExpired(),
                user.getAccountNotLocked(),
                user.getCredentialNotExpired(),
                authorityList);
    }
    public Authentication authenticate(String email, String password) {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        if (!user.getAccountNotLocked()) {
            throw new LockedException("Account locked due to multiple failed login attempts.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleFailedLogin(user);
            throw new RuntimeException("Incorrect credentials.");
        }

        resetFailedAttempts(user);

        UserDetails userDetails = this.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
    /**
     * Inicia sesión del usuario, genera un JWT y lo retorna junto con el estado de autenticación.
     *
     * @param authLoginRequest objeto con email y password
     * @return DTO con email, mensaje, JWT y estado
     */
    public AuthResponseDTO loginUser(AuthLoginRequestDTO authLoginRequest){

        String email = authLoginRequest.email();
        String password = authLoginRequest.password();
        Authentication authentication = this.authenticate(email, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponseDTO(email,"Login seccesfull", accessToken, true);
    }
    /**
     * Maneja un intento de login fallido, aumentando el contador y bloqueando la cuenta si supera el límite.
     *
     * @param user usuario a actualizar
     */
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= 3) {
            user.setAccountNotLocked(false);// Bloquea la cuenta
            user.setCredentialNotExpired(false);
            userRepository.save(user);
            throw new LockedException("Account locked due to multiple failed login attempts.");
        }

        userRepository.save(user);
    }
    /**
     * Reinicia el contador de intentos fallidos si el login es exitoso.
     *
     * @param user usuario autenticado
     */
    private void resetFailedAttempts(User user) {
        if (user.getFailedAttempts() > 0) {
            user.setFailedAttempts(0);
            userRepository.save(user);
        }
    }
}
