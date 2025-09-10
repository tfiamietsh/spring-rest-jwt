package core.controller;

import core.entity.User;
import core.service.AuthService;
import core.service.JwtService;
import core.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserService userService, JwtService jwtService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> postRegister(@Valid @RequestBody User user) {
        try {
            userService.loadUserByUsername(user.getUsername());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь уже существует");
        } catch (UsernameNotFoundException e) {
            userService.register(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> postLogin(@RequestBody User user) {
        authService.authenticate(user);

        User authenticatedUser = authService.getAuthenticatedUser();
        String jwt = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jwt);
    }
}
