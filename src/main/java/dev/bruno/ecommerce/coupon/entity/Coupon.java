package dev.bruno.ecommerce.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotNull
    @Future
    @Column(nullable = false)
    private OffsetDateTime expirationDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Long creatorId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    public BigDecimal applyTo(BigDecimal total) {
        BigDecimal multiplier = BigDecimal.ONE.subtract(
                discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );

        return total.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
