package dev.bruno.ecommerce.config;

import dev.bruno.ecommerce.user.dto.RoleType;
import dev.bruno.ecommerce.user.entity.User;
import dev.bruno.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        User existingAdmin = userRepository.findByLogin("admin");

        if (existingAdmin != null) {
            existingAdmin.setRoleType(RoleType.ADMIN);
            userRepository.save(existingAdmin);
            return;
        }

        User admin = User.builder()
                .login("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roleType(RoleType.ADMIN)
                .build();

        userRepository.save(admin);
    }
}
