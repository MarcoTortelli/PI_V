package dev.bruno.ecommerce.user.service;

import dev.bruno.ecommerce.user.dto.UserRegisterRequest;
import dev.bruno.ecommerce.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {
    public User toUser(UserRegisterRequest request) {
        return User.builder()
                .login(request.login())
                .build();
    }
}
