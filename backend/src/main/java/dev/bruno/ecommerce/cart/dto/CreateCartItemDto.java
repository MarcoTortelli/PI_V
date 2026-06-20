package dev.bruno.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemDto(
        @NotNull Long productId,
        @Positive Integer quantity
) {
}
