package core.mapper;

import core.dto.user.UserInfoDto;
import core.entity.User;

public class UserMapper {
    public static UserInfoDto toInfoDto(User user) {
        return new UserInfoDto(user.getId(), user.getUsername(), user.getRole().getDisplayName(),
                user.getRegistrationTimestamp());
    }
}
