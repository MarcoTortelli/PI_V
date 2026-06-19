package dev.bruno.ecommerce.coupon.service;

import dev.bruno.ecommerce.coupon.dto.CouponRequest;
import dev.bruno.ecommerce.coupon.dto.CouponResponse;
import dev.bruno.ecommerce.coupon.entity.Coupon;
import dev.bruno.ecommerce.coupon.repository.CouponRepository;
import dev.bruno.ecommerce.exception.InvalidCouponException;
import dev.bruno.ecommerce.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository repository;
    private final CouponMapper mapper;

    public CouponResponse create(User user, CouponRequest request) {
        if (repository.existsCouponByCode(request.code())) {
            throw new InvalidCouponException("Coupon code already exists.");
        }

        Coupon coupon = mapper.toCoupon(request);
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setCreatorId(user.getId());

        repository.save(coupon);

        return mapper.toDto(coupon);
    }

    public CouponResponse findCouponByCode(String couponCode) {
        return mapper.toDto(findEntityByCode(couponCode));
    }

    public void delete(String couponCode) {
        Coupon coupon = findEntityByCode(couponCode);

        repository.delete(coupon);
    }

    public Coupon findEntityByCode(String couponCode) {
        return repository.findCouponByCode(couponCode)
                .orElseThrow(() -> new InvalidCouponException(
                        String.format("Coupon with code %s not found.", couponCode)));
    }
}
