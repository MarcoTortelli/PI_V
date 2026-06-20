package dev.bruno.ecommerce.coupon.service;

import dev.bruno.ecommerce.coupon.dto.CouponRequest;
import dev.bruno.ecommerce.coupon.dto.CouponResponse;
import dev.bruno.ecommerce.coupon.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    public Coupon toCoupon(CouponRequest request) {
        return Coupon.builder().code(request.code()).expirationDate(request.expirationDate()).discountPercentage(request.discountPercentage()).active(true).build();
    }

    public CouponResponse toDto(Coupon coupon) {
        return new CouponResponse(
            coupon.getCode(), coupon.getDiscountPercentage(), coupon.getExpirationDate()
        );
    }
}
