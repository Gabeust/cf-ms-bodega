package com.gabeust.notificationservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayAccessFilter extends OncePerRequestFilter {

    @Value("${api.gateway.secret}")
    private String gatewaySecret;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ⚠️ Este log ayuda mucho para saber qué está interceptando el filtro
        System.out.println("Request intercepted: " + path);

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
