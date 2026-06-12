package dev.bruno.ecommerce.cart.repository;

import dev.bruno.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByIdAndUserId(Long orderId, Long userId);
}
