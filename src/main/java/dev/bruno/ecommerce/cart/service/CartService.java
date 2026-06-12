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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CouponGateway couponGateway;

    @Transactional
    public CartResponse createCart(CreateCartDto cartDto, User user) {
        List<CartItem> cartItems = new ArrayList<>();

        for (CreateCartItemDto cartItemDto : cartDto.cartItems()) {
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

        for (CreateCartItemDto cartItemDto : cartDto.cartItems()) {

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

        cart.setCartItems(cartItems);

        cart.calculateTotal();

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDto(
                savedCart,
                toResponse(cart)
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

    public void deleteCartById(Long cartId, Long userId) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Entity with ID %d not found.", cartId)));

        cartRepository.delete(cart);
    }

    @Transactional
    public CartResponse applyCoupon(Long cartId, String code, Long userId) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cart with ID %d not found.", cartId)));

        Coupon coupon = couponGateway.findByCode(code);

        if (!coupon.getActive()) {
            throw new InvalidCouponException("Coupon is inactive.");
        }

        if (coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Coupon is expired.");
        }

        cart.setCoupon(coupon);
        cart.calculateTotal();

        return cartMapper.toDto(cartRepository.save(cart), toResponse(cart));
    }

    private List<CartItemDto> toResponse(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItemMapper::toDto)
                .toList();
    }
}