package dev.bruno.ecommerce.coupon.repository;

import dev.bruno.ecommerce.coupon.entity.Coupon;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    boolean existsCouponByCode(@NotBlank(message = "Coupon code cannot be blank.") String code);

    Optional<Coupon> findCouponByCode(String couponCode);
}
