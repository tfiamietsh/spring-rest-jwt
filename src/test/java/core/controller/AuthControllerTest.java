package core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.dto.user.UserAuthDto;
import core.entity.User;
import core.service.AuthService;
import core.service.JwtService;
import core.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private static final String TEST_USERNAME = "valid-username", TEST_PASSWORD = "val1D_password";
    private static final String TEST_JWT = "test_jwt";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private ObjectMapper objectMapper;
    private UserAuthDto userAuthDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userAuthDto = new UserAuthDto(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    void shouldReturnConflictIfUserExistsWhenPostRegister() throws Exception {
        when(userService.loadUserByUsername(userAuthDto.username())).thenReturn(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCreateUserIfNotExistsWhenPostRegister() throws Exception {
        when(userService.loadUserByUsername(userAuthDto.username())).thenThrow(UsernameNotFoundException.class);

        doNothing().when(userService).register(userAuthDto);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthDto)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).register(userAuthDto);
    }

    @Test
    void shouldReturnJwtTokenOnSuccessWhenPostLogin() throws Exception {
        Authentication auth = mock(Authentication.class);
        User authenticatedUser = new User();

        when(authService.authenticateUserWithCredentials(userAuthDto)).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(authenticatedUser);
        when(jwtService.generateToken(authenticatedUser)).thenReturn(TEST_JWT);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthDto)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(TEST_JWT));
    }
}
