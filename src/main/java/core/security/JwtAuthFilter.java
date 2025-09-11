package core.security;

import core.entity.User;
import core.service.AuthService;
import core.service.JwtService;
import core.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String JWT_PREFIX = "Bearer ";
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthService authService;

    public JwtAuthFilter(UserService userService, JwtService jwtService, AuthService authService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(JWT_PREFIX.length());
        String username = jwtService.extractUsername(jwt);

        if (username != null && authService.getAuthentication() == null) {
            User user = userService.loadUserByUsername(username);

            if (jwtService.validateToken(jwt, user))
                authService.setAuthenticationForUser(user, request);
        }

        filterChain.doFilter(request, response);
    }
}
