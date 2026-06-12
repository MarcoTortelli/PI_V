package dev.bruno.ecommerce.cart.service;

import dev.bruno.ecommerce.cart.entity.CartItem;
import dev.bruno.ecommerce.cart.dto.CartItemDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartItemMapper {
    public CartItemDto toDto(CartItem cartItem) {
        return new CartItemDto(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getPriceAtPurchase(),
                cartItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
        );
    }
}
