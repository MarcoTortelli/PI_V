package dev.bruno.ecommerce.cart.service;

import dev.bruno.ecommerce.cart.entity.Cart;
import dev.bruno.ecommerce.cart.dto.CartResponse;
import dev.bruno.ecommerce.cart.dto.CartItemDto;
import dev.bruno.ecommerce.payment.dto.PaymentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartMapper {

    public CartResponse toDto(Cart cart, List<CartItemDto> orderItemsDto) {
        return new CartResponse(
                cart.getId(),
                cart.getCreatedAt(),
                cart.getTotal(),
                cart.getCoupon() != null ? cart.getCoupon().getCode() : null,
                orderItemsDto,
                cart.getPaid()
        );
    }
}
