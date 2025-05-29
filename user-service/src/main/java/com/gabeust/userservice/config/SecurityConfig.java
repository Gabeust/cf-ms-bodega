package com.gabeust.userservice.config;

import com.gabeust.userservice.config.filter.JwtValidator;
import com.gabeust.userservice.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
/**
 * Configuración de seguridad para el servicio de usuarios.
 *
 * Define la política de seguridad del sistema, incluyendo:
 * - Autenticación mediante JWT
 * - Política sin estado para sesiones
 * - Filtro personalizado para validación de JWT
 * - Codificador de contraseñas
 * - Proveedor de autenticación personalizado
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    /**
     * Configura la cadena de filtros de seguridad.
     *
     * - Desactiva CSRF
     * - Usa sesiones sin estado (JWT)
     * - Agrega filtro personalizado para validar JWT
     * - Habilita autenticación básica HTTP
     *
     * @param httpSecurity el objeto HttpSecurity
     * @return la cadena de filtros configurada
     * @throws Exception en caso de error de configuración
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtValidator(jwtUtils), BasicAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();

    }
    /**
     * Provee el AuthenticationManager del contexto.
     *
     * @param configuration configuración de autenticación
     * @return el AuthenticationManager configurado
     * @throws Exception si hay error al obtenerlo
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return  configuration.getAuthenticationManager();
    }
    /**
     * Codificador de contraseñas usando BCrypt.
     *
     * @return el PasswordEncoder
     */
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    /**
     * Proveedor de autenticación personalizado con servicio de usuario y codificador.
     *
     * @param userDetailsService servicio para cargar usuarios
     * @return el AuthenticationProvider configurado
     */
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
