package dev.bruno.ecommerce.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        LocalDateTime createdAt,
        BigDecimal total,
        String couponCode,
        List<CartItemDto> cartItems,
        Boolean paid
) {}
