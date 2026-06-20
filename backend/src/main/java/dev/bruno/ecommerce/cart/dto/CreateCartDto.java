package dev.bruno.ecommerce.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateCartDto(
        @NotNull(message = "Cart items cannot be null")
        @Valid @Size(min = 1, message = "Cart must contain at least one item")
        List<CreateCartItemDto> cartItems
) {}
