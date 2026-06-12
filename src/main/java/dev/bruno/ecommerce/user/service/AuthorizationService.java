package dev.bruno.ecommerce.user.service;

import dev.bruno.ecommerce.user.entity.User;
import dev.bruno.ecommerce.user.dto.AuthenticationDto;
import dev.bruno.ecommerce.user.dto.UserRegisterRequest;
import dev.bruno.ecommerce.user.dto.TokenResponse;
import dev.bruno.ecommerce.exception.UserAlreadyExists;
import dev.bruno.ecommerce.user.repository.UserRepository;
import dev.bruno.ecommerce.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final UserMapper mapper;
    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(@Valid AuthenticationDto request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return new TokenResponse(token);
    }

    public TokenResponse register(@Valid UserRegisterRequest request) {
        if (this.repository.findByLogin(request.login()) != null)
            throw new UserAlreadyExists("User already exists in database.");

        String encryptedPassword = passwordEncoder.encode(request.password());
        User user = mapper.toUser(request);
        user.setPassword(encryptedPassword);

        repository.save(user);

        return login(new AuthenticationDto(request.login(), request.password()));
    }
}
