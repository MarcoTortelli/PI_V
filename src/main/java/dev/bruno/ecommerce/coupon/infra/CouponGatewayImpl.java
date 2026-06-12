package dev.bruno.ecommerce.coupon.infra;

import dev.bruno.ecommerce.cart.gateway.CouponGateway;
import dev.bruno.ecommerce.coupon.entity.Coupon;
import dev.bruno.ecommerce.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponGatewayImpl implements CouponGateway {
    private final CouponService service;

    @Override
    public Coupon findByCode(String couponCode) {
        return service.findEntityByCode(couponCode);
    }
}
