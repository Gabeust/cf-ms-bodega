package com.gabeust.cartservice.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Filtro de seguridad que asegura que todas las solicitudes provengan exclusivamente del API Gateway.
 *
 * Este filtro verifica que la cabecera personalizada {@code X-Gateway-Auth} esté presente
 * y coincida con el valor secreto configurado en {@code api.gateway.secret}.
 *
 * Si la validación falla, se devuelve una respuesta HTTP 401 (Unauthorized).
 */
@Component
public class GatewayAccessFilter extends OncePerRequestFilter {
    /**
     * Valor secreto compartido entre el API Gateway y este microservicio.
     * Se configura mediante la propiedad {@code api.gateway.secret}.
     */
    @Value("${api.gateway.secret}")
    private String gatewaySecret;
    /**
     * Ejecuta la lógica de validación del header {@code X-Gateway-Auth}.
     *
     * @param request     la solicitud HTTP entrante
     * @param response    la respuesta HTTP saliente
     * @param filterChain la cadena de filtros
     * @throws ServletException en caso de error en el filtro
     * @throws IOException      en caso de error de E/S
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String header = request.getHeader("X-Gateway-Auth");

        if (header == null || !header.equals(gatewaySecret)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - must come through API Gateway");
            return;
        }


        filterChain.doFilter(request, response);
    }


}
