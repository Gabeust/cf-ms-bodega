package com.gabeust.userservice.controller;


import com.gabeust.userservice.dto.AuthLoginRequestDTO;
import com.gabeust.userservice.dto.AuthResponseDTO;
import com.gabeust.userservice.dto.ChangePasswordRequest;
import com.gabeust.userservice.entity.User;
import com.gabeust.userservice.service.PasswordResetService;
import com.gabeust.userservice.service.UserDetailsServiceImpl;
import com.gabeust.userservice.service.UserServiceImpl;
import com.gabeust.userservice.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y operaciones relacionadas con la seguridad.
 *
 * Provee endpoints para login, logout, cambio y restablecimiento de contraseña.
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordResetService passwordResetService;
    private final JwtUtils jwtUtils;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsServiceImpl userDetailsService, PasswordResetService passwordResetService, JwtUtils jwtUtils, UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordResetService = passwordResetService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Autentica al usuario y genera un token JWT si las credenciales son válidas.
     *
     * @param loginRequestDTO contiene email y contraseña
     * @return Token JWT y datos del usuario autenticado
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthLoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(this.userDetailsService.loginUser(loginRequestDTO), HttpStatus.OK);
    }
    /**
     * Invalida el token JWT del usuario (logout manual).
     *
     * @param token el token JWT del encabezado Authorization
     * @return Mensaje de éxito
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        jwtUtils.invalidateToken(jwt);
        return ResponseEntity.ok("Logged out successfully.!");
    }

    /**
     * Restablece la contraseña de un usuario usando un token válido.
     *
     * @param token token de restablecimiento
     * @param newPassword nueva contraseña
     * @return Mensaje de confirmación o error
     */
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid or expired token.");
        }
    }
    /**
     * Permite a un usuario autenticado cambiar su contraseña actual.
     *
     * @param request objeto con contraseña actual y nueva
     * @param authentication contexto de autenticación (obtenido automáticamente)
     * @return Mensaje indicando el resultado del cambio
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authenticated.");
        }
        // Obtener email del usuario autenticado

        String email = authentication.getName();

        // Buscar usuario en la base de datos
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Verificar la contraseña actual
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect current password.");
        }
        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userService.save(user);

        return ResponseEntity.ok("Password changed successfully.");
    }

}
