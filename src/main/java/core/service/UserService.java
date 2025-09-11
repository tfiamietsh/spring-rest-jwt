package core.service;

import core.dto.user.UserAuthDto;
import core.dto.user.UserInfoDto;
import core.entity.User;
import core.enums.Role;
import core.exception.InvalidRoleChangeException;
import core.mapper.UserMapper;
import core.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleHierarchy roleHierarchy;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleHierarchy roleHierarchy) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public List<UserInfoDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toInfoDto)
                .collect(Collectors.toList());
    }

    public void register(UserAuthDto userAuthDto) {
        User user = new User();

        user.setUsername(userAuthDto.username());
        user.setPassword(passwordEncoder.encode(userAuthDto.password()));
        user.setRole(Role.USER);
        user.setRegistrationTimestamp(OffsetDateTime.now());

        userRepository.save(user);
    }

    private boolean isRoleHigherOrEqual(Role higherRole, Role lowerRole) {
        return roleHierarchy.getReachableGrantedAuthorities(List.of(higherRole.toGrantedAuthority()))
                .contains(lowerRole.toGrantedAuthority());
    }

    public void promote(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (user.getRole().equals(role))
            throw new InvalidRoleChangeException("Пользователь уже имеет такую роль");
        else if (isRoleHigherOrEqual(user.getRole(), role))
            throw new InvalidRoleChangeException("Невозможно изменить роль на более низкую");

        user.setRole(role);

        userRepository.save(user);
    }
}
