package core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@AllArgsConstructor
@Getter
public enum Role {
    USER("USER", "Пользователь"),
    STAFF("STAFF", "Модератор"),
    ADMIN("ADMIN", "Администратор");

    private final String name, displayName;

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + getName());
    }
}
