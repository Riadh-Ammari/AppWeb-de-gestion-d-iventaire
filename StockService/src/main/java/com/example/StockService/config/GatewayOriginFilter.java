package com.example.StockService.config;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayOriginFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response); // Allow OPTIONS requests without checking header
            return;
        }

        String originHeader = request.getHeader("X-Gateway-Origin");
        if (!"api-gateway".equals(originHeader)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Request must come from API Gateway");
            return;
        }
        filterChain.doFilter(request, response);
    }
}