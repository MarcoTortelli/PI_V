package dev.bruno.ecommerce.payment.service;

import dev.bruno.ecommerce.cart.entity.Cart;
import dev.bruno.ecommerce.payment.entity.Payment;
import dev.bruno.ecommerce.payment.dto.CreatePaymentDto;
import dev.bruno.ecommerce.payment.dto.PaymentDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

    public Payment toPayment(CreatePaymentDto createPaymentDto, Cart cart) {
        return Payment.builder()
                .cart(cart)
                .paymentMethod(createPaymentDto.paymentMethod())
                .cardInformation(createPaymentDto.cardInformation())
                .build();
    }

    public PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getCart().getId(),
                payment.getPaymentMethod(),
                payment.getAmountPaid()
        );
    }
}
