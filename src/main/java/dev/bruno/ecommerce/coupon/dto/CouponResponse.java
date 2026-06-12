package dev.bruno.ecommerce.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        String code,
        BigDecimal discountPercentage,
        LocalDateTime expirationDate
) {}