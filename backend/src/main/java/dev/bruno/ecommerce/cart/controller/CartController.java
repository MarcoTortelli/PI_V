package dev.bruno.ecommerce.cart.controller;

import dev.bruno.ecommerce.cart.dto.CartResponse;
import dev.bruno.ecommerce.cart.service.CartService;
import dev.bruno.ecommerce.user.entity.User;
import dev.bruno.ecommerce.cart.dto.CreateCartDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Cart management endpoints")
@RequiredArgsConstructor
public class CartController {
    private final CartService service;

    @Operation(
            summary = "Create cart",
            description = "Creates a new cart for the authenticated user using the provided products."
    )
    @PostMapping("/create-cart")
    public ResponseEntity<CartResponse> createCart(
            @RequestBody @Valid CreateCartDto createCartDto,
            @AuthenticationPrincipal User user
    ) {
        CartResponse cartResponse = service.createCart(createCartDto, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(cartResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);
    }

    @Operation(
            summary = "Edit cart",
            description = "Edits an existing cart by adding, removing or updating products."
    )
    @PutMapping("/edit-cart/{cartId}")
    public ResponseEntity<CartResponse> editCart(
            @PathVariable Long cartId,
            @RequestBody @Valid CreateCartDto createCartDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.editCart(cartId, createCartDto, user.getId()));
    }

    @Operation(
            summary = "Apply coupon",
            description = "Applies a coupon code to the cart and recalculates the total."
    )
    @PatchMapping("/{cartId}/coupons")
    public ResponseEntity<CartResponse> applyCoupon(
            @PathVariable Long cartId,
            @RequestParam String code,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.applyCoupon(cartId, code, user.getId()));
    }

    @Operation(
            summary = "Get user carts",
            description = "Retrieves all carts belonging to the authenticated user."
    )
    @GetMapping("/me")
    public ResponseEntity<List<CartResponse>> getUserCarts(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.findUserCarts(user.getId()));
    }

    @Operation(
            summary = "Find cart by ID",
            description = "Retrieves a cart belonging to the authenticated user using its unique identifier."
    )
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.findCartById(cartId, user.getId()));
    }

    @Operation(
            summary = "Delete cart",
            description = "Deletes a cart belonging to the authenticated user."
    )
    @DeleteMapping("/delete-cart/{cartId}")
    public ResponseEntity<Void> deleteCartById(
            @PathVariable Long cartId,
            @AuthenticationPrincipal User user
    ) {
        service.deleteCartById(cartId, user.getId());

        return ResponseEntity.noContent().build();
    }
}