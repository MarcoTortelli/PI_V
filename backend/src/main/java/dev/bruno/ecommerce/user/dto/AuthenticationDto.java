package dev.bruno.ecommerce.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDto(
        @NotBlank(message = "Login cannot be blank.")
        String login,

        @NotBlank(message = "Password cannot be blank.")
        String password
) {
}
