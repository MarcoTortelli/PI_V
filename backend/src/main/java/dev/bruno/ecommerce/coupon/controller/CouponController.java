package dev.bruno.ecommerce.coupon.controller;

import dev.bruno.ecommerce.coupon.dto.CouponRequest;
import dev.bruno.ecommerce.coupon.dto.CouponResponse;
import dev.bruno.ecommerce.coupon.service.CouponService;
import dev.bruno.ecommerce.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon management endpoints")
public class CouponController {
    private final CouponService service;

    @Operation(
            summary = "Create coupon",
            description = "Creates a new coupon for the authenticated user."
    )
    @PostMapping
    public ResponseEntity<CouponResponse> create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CouponRequest request
    ) {
        return ResponseEntity.ok(service.create(user, request));
    }

    @Operation(
            summary = "Create coupon using query params",
            description = "Creates a new coupon using query parameters."
    )
    @PostMapping(params = {"code", "discountPercentage", "expirationDate"})
    public ResponseEntity<CouponResponse> createFromParams(
            @AuthenticationPrincipal User user,
            @RequestParam String code,
            @RequestParam BigDecimal discountPercentage,
            @RequestParam OffsetDateTime expirationDate
    ) {
        CouponRequest request = new CouponRequest(
                code,
                discountPercentage,
                expirationDate
        );

        return ResponseEntity.ok(service.create(user, request));
    }

    @Operation(
            summary = "Find coupon by code",
            description = "Retrieves coupon details using its unique code."
    )
    @GetMapping("/{couponCode}")
    public ResponseEntity<CouponResponse> findByCouponCode(
            @PathVariable String couponCode
    ) {
        return ResponseEntity.ok(service.findCouponByCode(couponCode));
    }

    @Operation(
            summary = "Delete coupon",
            description = "Deletes an existing coupon by its code."
    )
    @DeleteMapping("/{couponCode}")
    public ResponseEntity<Void> delete(
            @PathVariable String couponCode
    ) {
        service.delete(couponCode);
        return ResponseEntity.noContent().build();
    }
}
