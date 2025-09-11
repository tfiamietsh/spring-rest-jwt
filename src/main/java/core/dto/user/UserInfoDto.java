package core.dto.user;

import java.time.OffsetDateTime;

public record UserInfoDto(Long id, String username, String role, OffsetDateTime registeredAt) {}
