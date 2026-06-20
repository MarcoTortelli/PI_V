package dev.bruno.ecommerce.cart.service;

import dev.bruno.ecommerce.cart.dto.CreateCartDto;
import dev.bruno.ecommerce.cart.dto.CreateCartItemDto;
import dev.bruno.ecommerce.cart.dto.CartResponse;
import dev.bruno.ecommerce.cart.dto.CartItemDto;
import dev.bruno.ecommerce.cart.entity.Cart;
import dev.bruno.ecommerce.cart.entity.CartItem;
import dev.bruno.ecommerce.cart.gateway.CouponGateway;
import dev.bruno.ecommerce.coupon.entity.Coupon;
import dev.bruno.ecommerce.exception.InvalidCouponException;
import dev.bruno.ecommerce.payment.entity.Payment;
import dev.bruno.ecommerce.payment.repository.PaymentRepository;
import dev.bruno.ecommerce.product.entity.Product;
import dev.bruno.ecommerce.exception.EntityNotFoundException;
import dev.bruno.ecommerce.exception.InsufficientStockException;
import dev.bruno.ecommerce.cart.repository.CartRepository;
import dev.bruno.ecommerce.product.repository.ProductRepository;
import dev.bruno.ecommerce.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CouponGateway couponGateway;
    private final PaymentRepository paymentRepository;

    @Transactional
    public CartResponse createCart(CreateCartDto cartDto, User user) {
        Optional<Cart> existingCart =
                cartRepository.findByUserIdAndPaidFalse(user.getId());

        if (existingCart.isPresent()) {
            throw new IllegalStateException(
                    "User already has an active cart."
            );
        }
        List<CartItem> cartItems = new ArrayList<>();

        for (CreateCartItemDto cartItemDto : normalizeItems(cartDto.cartItems())) {
            Product product = productRepository.findById(cartItemDto.productId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartItemDto.productId())));

            if (product.getQuantity() <= 0 || product.getQuantity() < cartItemDto.quantity()) {
                throw new InsufficientStockException(product.getName());
            }

            CartItem item = CartItem.builder()
                    .product(product)
                    .quantity(cartItemDto.quantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            cartItems.add(item);
        }

        Cart cart = new Cart(LocalDateTime.now(), cartItems);

        cartItems.forEach(item -> item.setCart(cart));

        cart.calculateTotal();

        cart.setUser(user);

        return cartMapper.toDto(cartRepository.save(cart), toResponse(cart));
    }

    @Transactional
    public CartResponse editCart(Long cartId, CreateCartDto cartDto, Long userId) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartId)));

        cart.getCartItems().clear();

        List<CartItem> cartItems = new ArrayList<>();

        for (CreateCartItemDto cartItemDto : normalizeItems(cartDto.cartItems())) {

            Product product = productRepository.findById(cartItemDto.productId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartItemDto.productId())));

            if (product.getQuantity() <= 0 ||
                    product.getQuantity() < cartItemDto.quantity()) {

                throw new InsufficientStockException(product.getName());
            }

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(cartItemDto.quantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            cartItems.add(item);
        }

        cart.getCartItems().addAll(cartItems);

        cart.calculateTotal();

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDto(
                savedCart,
                toResponse(savedCart)
        );
    }

    public List<CartResponse> findUserCarts(Long userId) {
        List<Cart> userCarts = cartRepository.findByUserId(userId);

        return userCarts.stream()
                .map(cart -> cartMapper.toDto(
                        cart,
                        toResponse(cart)
                )).toList();
    }

    public CartResponse findCartById(Long cartId, Long userId) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartId)));

        return cartMapper.toDto(
                cart,
                toResponse(cart)
        );
    }

    @Transactional
    public void deleteCartById(Long cartId, Long userId) {

        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartId)));

        List<Payment> payments = paymentRepository.findByCartId(cartId);

        if (!payments.isEmpty()) {
            cart.setPayment(null);
            cartRepository.saveAndFlush(cart);
            paymentRepository.deleteAll(payments);
        }

        cartRepository.delete(cart);
    }

    @Transactional
    public CartResponse applyCoupon(Long cartId, String code, Long userId) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cart with ID %d not found.", cartId)));

        Coupon coupon = couponGateway.findByCode(code);

        if (coupon == null) {
            throw new InvalidCouponException(
                    "Coupon not found."
            );
        }

        if (!coupon.getActive()) {
            throw new InvalidCouponException("Coupon is inactive.");
        }

        if (coupon.getExpirationDate().isBefore(OffsetDateTime.now())) {
            throw new InvalidCouponException("Coupon is expired.");
        }

        cart.setCoupon(coupon);
        cart.calculateTotal();

        Cart savedCart = cartRepository.saveAndFlush(cart);

        return cartMapper.toDto(savedCart, toResponse(savedCart));
    }

    private List<CartItemDto> toResponse(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    private List<CreateCartItemDto> normalizeItems(List<CreateCartItemDto> items) {
        Map<Long, Integer> quantityByProductId = new LinkedHashMap<>();

        for (CreateCartItemDto item : items) {
            quantityByProductId.merge(item.productId(), item.quantity(), Integer::sum);
        }

        return quantityByProductId.entrySet()
                .stream()
                .map(entry -> new CreateCartItemDto(entry.getKey(), entry.getValue()))
                .toList();
    }
}
