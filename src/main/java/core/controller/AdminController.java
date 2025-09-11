package core.controller;

import core.dto.user.UserInfoDto;
import core.enums.Role;
import core.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfoDto>> getUsers() {
        List<UserInfoDto> users = userService.getAllUsers();

        if (users.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/promote/{id}")
    public ResponseEntity<String> patchPromote(@PathVariable Long id) {
        userService.promote(id, Role.STAFF);

        return ResponseEntity.ok("Пользователь успешно повышен до модератора");
    }
}
