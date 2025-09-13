package core.service;

import core.dto.user.UserAuthDto;
import core.entity.User;
import core.enums.Role;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class AuthServiceTest {
    private static final String TEST_USERNAME = "test_username", TEST_PASSWORD = "test_password";
    private static final Role TEST_ROLE = Role.USER;
    private AuthenticationManager authenticationManager;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        authService = new AuthService(authenticationManager);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticationWhenAuthenticateUserWithCredentials() {
        UserAuthDto userAuthDto = new UserAuthDto(TEST_USERNAME, TEST_PASSWORD);

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);

        Authentication auth = authService.authenticateUserWithCredentials(userAuthDto);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertNotNull(auth);
        assertEquals(mockAuth, auth);
    }

    @Test
    void shouldSetAuthenticationInSecurityContextWhenSetAuthenticationForUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setRole(TEST_ROLE);

        HttpServletRequest request = mock(HttpServletRequest.class);
        authService.setAuthenticationForUser(user, request);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(TEST_USERNAME, auth.getPrincipal());
    }

    @Test
    void shouldReturnCurrentAuthenticationWhenGetAuthentication() {
        Authentication mockAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        Authentication auth = authService.getAuthentication();
        assertEquals(mockAuth, auth);
    }
}
