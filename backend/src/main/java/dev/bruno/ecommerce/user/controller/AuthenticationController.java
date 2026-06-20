package dev.bruno.ecommerce.user.controller;

import dev.bruno.ecommerce.user.dto.AuthenticationDto;
import dev.bruno.ecommerce.user.dto.UserRegisterRequest;
import dev.bruno.ecommerce.user.dto.TokenResponse;
import dev.bruno.ecommerce.user.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthenticationController {
    private final AuthorizationService authorizationService;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using login credentials and returns a JWT access token."
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid AuthenticationDto request) {
        return ResponseEntity.ok(authorizationService.login(request));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT access token."
    )
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return ResponseEntity.ok(authorizationService.register(request));
    }
}
