package dev.bruno.ecommerce.payment.service;

import dev.bruno.ecommerce.client.CardValidatorClient;
import dev.bruno.ecommerce.client.dto.ValidationResponse;
import dev.bruno.ecommerce.exception.InsufficientStockException;
import dev.bruno.ecommerce.cart.entity.Cart;
import dev.bruno.ecommerce.cart.entity.CartItem;
import dev.bruno.ecommerce.payment.entity.Payment;
import dev.bruno.ecommerce.payment.dto.CreatePaymentDto;
import dev.bruno.ecommerce.cart.dto.CartResponse;
import dev.bruno.ecommerce.cart.dto.CartItemDto;
import dev.bruno.ecommerce.payment.dto.PaymentDto;
import dev.bruno.ecommerce.exception.EntityNotFoundException;
import dev.bruno.ecommerce.exception.InvalidCardException;
import dev.bruno.ecommerce.exception.OrderAlreadyPaidException;
import dev.bruno.ecommerce.cart.repository.CartRepository;
import dev.bruno.ecommerce.payment.repository.PaymentRepository;
import dev.bruno.ecommerce.cart.service.CartItemMapper;
import dev.bruno.ecommerce.cart.service.CartMapper;
import dev.bruno.ecommerce.payment.dto.PaymentMethod;
import dev.bruno.ecommerce.product.entity.Product;
import dev.bruno.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CardValidatorClient cardValidatorClient;
    private final PaymentMapper paymentMapper;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    public PaymentDto findPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Entity with ID %d not found.", id))
        );

        return paymentMapper.toDto(payment);
    }

    public CartResponse processPayment(CreatePaymentDto createPaymentDto) {

        Cart cart = cartRepository.findById(createPaymentDto.orderId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Entity with ID %d not found.", createPaymentDto.orderId())
                )
        );

        if (cart.getPaid()) {
            throw new OrderAlreadyPaidException("Cart is already paid.");
        }

        if (
                createPaymentDto.paymentMethod().equals(PaymentMethod.DEBIT_CARD)
                        || createPaymentDto.paymentMethod().equals(PaymentMethod.CREDIT_CARD)
        ) {

            ValidationResponse response = cardValidatorClient.isValid().getBody();

            if (
                    response == null
                            || !response.isSuccess()
                            || createPaymentDto.cardInformation() == null
            ) {
                throw new InvalidCardException("Card information is invalid.");
            }
        }

        for (CartItem item : cart.getCartItems()) {

            Product product = item.getProduct();

            if (product.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(product.getName());
            }

            product.setQuantity(
                    product.getQuantity() - item.getQuantity()
            );

            productRepository.save(product);
        }

        Payment payment = paymentMapper.toPayment(createPaymentDto, cart);

        payment.setCart(cart);

        paymentRepository.save(payment);

        cart.setPaid(true);

        cartRepository.save(cart);

        List<CartItemDto> orderItemsDto = cart.getCartItems().stream()
                .map(cartItemMapper::toDto)
                .toList();

        return cartMapper.toDto(
                cart,
                orderItemsDto
        );
    }
}
