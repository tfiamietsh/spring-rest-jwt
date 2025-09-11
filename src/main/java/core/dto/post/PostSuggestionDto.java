package core.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostSuggestionDto(
        @NotBlank(message = "Заголовок не может быть пустым")
        @Size(max = 64, message = "Заголовок должен содержать не более 64 символов")
        String title,

        @NotBlank(message = "Текст не может быть пустым")
        String text
) {}
