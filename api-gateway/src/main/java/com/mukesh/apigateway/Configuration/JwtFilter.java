package com.mukesh.apigateway.Configuration;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public JwtFilter(JwtService jwtService) { this.jwtService = jwtService; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String token = authorization.substring(7);
            if (!jwtService.ValidateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired token");
                return;
            }
            String email = jwtService.ExtractEmailFromToken(token);
            String role = jwtService.ExtractRole(token);
            if (email == null || email.isBlank() || role == null || role.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token claims");
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(email, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))));
            CustomServletRequestWrapper trusted = new CustomServletRequestWrapper(request);
            trusted.addHeader("useremail", email);
            trusted.addHeader("role", role);
            chain.doFilter(trusted, response);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
