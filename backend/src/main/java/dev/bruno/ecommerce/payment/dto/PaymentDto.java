package dev.bruno.ecommerce.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentDto(
        @NotNull Long id,
        @NotNull Long orderId,
        @NotNull PaymentMethod paymentMethod,
        @NotNull BigDecimal amountPaid
) {}
