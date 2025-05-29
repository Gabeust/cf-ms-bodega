package com.gabeust.notificationservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Filtro que verifica que todas las peticiones a la aplicación provengan
 * exclusivamente a través del API Gateway.
 *
 * Válida que la cabecera HTTP "X-Gateway-Auth" contenga el secreto compartido
 * configurado en la propiedad "api.gateway.secret".
 *
 * Excluye del filtro las rutas relacionadas con WebSocket y SockJS para evitar
 * bloquear esas conexiones.
 *
 * Si la cabecera no está presente o no coincide, responde con 401 Unauthorized.
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
     * Método que intercepta cada petición HTTP y valida la cabecera "X-Gateway-Auth".
     *
     * Si la petición es para WebSocket o SockJS, se deja pasar sin validación.
     * Si la cabecera no es correcta o está ausente, se responde con 401.
     *
     * @param request  objeto HttpServletRequest con la información de la petición
     * @param response objeto HttpServletResponse para enviar respuesta al cliente
     * @param filterChain cadena de filtros para continuar el procesamiento si es válido
     * @throws ServletException excepción en el proceso del filtro
     * @throws IOException      excepción de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🛑 Excluimos todo lo relacionado a WebSocket y SockJS
        if (path.startsWith("/ws-stock-alerts") || path.contains("sockjs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("X-Gateway-Auth");

        if (header == null || !header.equals(gatewaySecret)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - must come through API Gateway");
            return;
        }

        filterChain.doFilter(request, response);
    }


}
