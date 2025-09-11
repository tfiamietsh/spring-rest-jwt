package core.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAuthDto(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(min = 4, max = 32, message = "Имя пользователя должно быть от 4 до 32 символов")
        @Pattern(
                regexp = "^[a-z][a-z0-9-]*$",
                message = "Имя пользователя может содержать только строчные латинские буквы, цифры и дефис"
        )
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 16, message = "Пароль должен быть от 8 до 16 символов")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Пароль должен содержать хотя бы одну цифру, одну заглавную и одну строчную латинские буквы"
        )
        String password
) {}
