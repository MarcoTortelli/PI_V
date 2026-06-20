package dev.bruno.ecommerce.payment.repository;

import dev.bruno.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCartId(Long cartId);
}
