package core.security;

import core.entity.User;
import core.service.AuthService;
import core.service.JwtService;
import core.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

public class JwtAuthFilterTest {
    private static final String TEST_JWT = "test_jwt";
    private static final String TEST_USERNAME = "test_username";
    private JwtAuthFilter jwtAuthFilter;
    private JwtService jwtService;
    private UserService userService;
    private AuthService authService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userService = mock(UserService.class);
        authService = mock(AuthService.class);
        jwtAuthFilter = new JwtAuthFilter(userService, jwtService, authService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldCallFilterChainIfNoAuthHeaderWhenDoFilterInternal() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userService, authService);
    }

    @Test
    void shouldAuthenticateWithValidTokenWhenDoFilterInternal() throws ServletException, IOException {
        String token = "Bearer " + TEST_JWT;
        User user = mock(User.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtService.extractUsername(TEST_JWT)).thenReturn(TEST_USERNAME);
        when(authService.getAuthentication()).thenReturn(null);
        when(userService.loadUserByUsername(TEST_USERNAME)).thenReturn(user);
        when(jwtService.validateToken(TEST_JWT, user)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(authService, times(1)).setAuthenticationForUser(user, request);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
