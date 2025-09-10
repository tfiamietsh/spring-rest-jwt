package core.controller;

import core.model.User;
import core.service.JwtService;
import core.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> postRegister(@Valid @RequestBody User user) {
        try {
            userService.loadUserByUsername(user.getUsername());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь уже существует");
        } catch (UsernameNotFoundException e) {
            userService.saveUser(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> postLogin(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        User authenticatedUser = (User) authentication.getPrincipal();
        String jwt = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jwt);
    }
}
