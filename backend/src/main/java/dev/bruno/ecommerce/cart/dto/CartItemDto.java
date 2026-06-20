package dev.bruno.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CartItemDto(
        @NotNull Long productId,
        @NotNull String productName,
        @Positive Integer quantity,
        @Positive BigDecimal priceAtPurchase,
        @Positive BigDecimal total
) {}
