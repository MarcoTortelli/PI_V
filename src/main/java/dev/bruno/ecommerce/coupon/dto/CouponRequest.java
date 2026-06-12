package dev.bruno.ecommerce.coupon.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(
        @NotBlank(message = "Coupon code cannot be blank.")
        String code,

        @DecimalMin("0.0")
        @DecimalMax("100.0")
        BigDecimal discountPercentage,

        @Future
        @NotNull
        LocalDateTime expirationDate
) {}