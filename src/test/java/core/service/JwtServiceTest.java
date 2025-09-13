package core.service;

import core.entity.User;

import io.jsonwebtoken.ExpiredJwtException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class JwtServiceTest {
    private static final String TEST_SECRET_KEY =
            "meatyriceballgivesyouplustwopercenttohpplusfiftytohpplusonetomeleeatkrecipeisricexfourchickenxfour";
    private static final long TEST_LIFETIME_MS = 1000;
    private static final String TEST_USERNAME = "test_username";
    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.setSecretKey(TEST_SECRET_KEY);
        jwtService.setLifetimeMs(TEST_LIFETIME_MS);

        user = new User();
        user.setUsername(TEST_USERNAME);
    }

    @Test
    void shouldGenerateNonNullTokenWhenGenerateToken() {
        assertNotNull(jwtService.generateToken(user));
    }

    @Test
    void shouldValidateTokenCorrectlyWhenValidTokenProvided() {
        assertTrue(jwtService.validateToken(jwtService.generateToken(user), user));
    }

    @Test
    void shouldExtractUsernameCorrectlyWhenTokenIsValid() {
        assertEquals(TEST_USERNAME, jwtService.extractUsername(jwtService.generateToken(user)));
    }

    @Test
    void shouldThrowExpiredJwtExceptionWhenTokenIsExpired() throws InterruptedException {
        String jwt = jwtService.generateToken(user);
        Thread.sleep(2 * TEST_LIFETIME_MS);

        assertThrowsExactly(ExpiredJwtException.class, () -> jwtService.validateToken(jwt, user));
    }
}
