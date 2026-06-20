package dev.bruno.ecommerce.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.bruno.ecommerce.cart.entity.Cart;
import dev.bruno.ecommerce.payment.dto.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_payment")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal amountPaid;

    @JsonIgnore
    private String cardInformation;

    public Payment() {
    }
}
