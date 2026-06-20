package dev.bruno.ecommerce.cart.gateway;

import dev.bruno.ecommerce.coupon.entity.Coupon;

public interface CouponGateway {
    Coupon findByCode(String code);
}
