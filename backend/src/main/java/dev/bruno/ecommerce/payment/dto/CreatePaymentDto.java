package dev.bruno.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentDto(
        @NotNull Long orderId,
        @NotNull PaymentMethod paymentMethod,
        @NotBlank String cardInformation
) {}
