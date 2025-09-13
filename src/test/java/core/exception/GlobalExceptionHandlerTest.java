package core.exception;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    private static final String TEST_MESSAGE = "test_exception_message";
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnListOfErrorMessagesWhenMethodArgumentNotValidExceptionHandler() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        ObjectError error = new ObjectError("test_object_error", TEST_MESSAGE);

        when(e.getAllErrors()).thenReturn(List.of(error));

        ResponseEntity<List<String>> response = exceptionHandler.methodArgumentNotValidExceptionHandler(e);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());

        List<String> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(TEST_MESSAGE, body.get(0));
    }

    @Test
    void shouldReturnMessageWithNotAcceptableStatusWhenBadCredentialsExceptionHandler() {
        BadCredentialsException e = new BadCredentialsException(TEST_MESSAGE);
        ResponseEntity<String> response = exceptionHandler.badCredentialsExceptionHandler(e);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(TEST_MESSAGE, response.getBody());
    }

    @Test
    void shouldReturnMessageWithConflictStatusWhenUsernameNotFoundExceptionHandler() {
        UsernameNotFoundException e = new UsernameNotFoundException(TEST_MESSAGE);
        ResponseEntity<String> response = exceptionHandler.usernameNotFoundExceptionHandler(e);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(TEST_MESSAGE, response.getBody());
    }

    @Test
    void shouldReturnMessageWithNotFoundStatusWhenEntityNotFoundExceptionHandler() {
        EntityNotFoundException e = new EntityNotFoundException(TEST_MESSAGE);
        ResponseEntity<String> response = exceptionHandler.entityNotFoundExceptionHandler(e);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(TEST_MESSAGE, response.getBody());
    }

    @Test
    void shouldReturnMessageWithConflictStatusWhenInvalidRoleChangeExceptionHandler() {
        InvalidRoleChangeException e = new InvalidRoleChangeException(TEST_MESSAGE);
        ResponseEntity<String> response = exceptionHandler.invalidRoleChangeExceptionHandler(e);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(TEST_MESSAGE, response.getBody());
    }
}
