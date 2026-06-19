package dev.bruno.ecommerce.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record CouponResponse(
        String code,
        BigDecimal discountPercentage,
        OffsetDateTime expirationDate
) {}