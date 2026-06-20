package dev.bruno.ecommerce.cart.repository;

import dev.bruno.ecommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
