package dev.bruno.ecommerce.cart.entity;

import dev.bruno.ecommerce.coupon.entity.Coupon;
import dev.bruno.ecommerce.payment.entity.Payment;
import dev.bruno.ecommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column
    private Boolean paid = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    public Cart(LocalDateTime createdAt, List<CartItem> cartItems) {
        this.createdAt = createdAt;
        this.cartItems = cartItems;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void calculateTotal() {
        List<CartItem> items = this.cartItems == null ? List.of() : this.cartItems;

        this.total = items.stream()
                .map(
                        cartItem -> cartItem.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        if (this.coupon != null) this.total = coupon.applyTo(this.total);
    }
}
