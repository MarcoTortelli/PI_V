package dev.bruno.ecommerce.payment.controller;

import dev.bruno.ecommerce.payment.dto.CreatePaymentDto;
import dev.bruno.ecommerce.cart.dto.CartResponse;
import dev.bruno.ecommerce.payment.dto.PaymentDto;
import dev.bruno.ecommerce.payment.service.PaymentService;
import dev.bruno.ecommerce.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/payment")
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Find payment by ID",
            description = "Retrieves payment details using its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable long id) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }

    @Operation(
            summary = "Process payment",
            description = "Processes the payment of an existing cart and marks the cart as paid."
    )
    @PostMapping("/charge")
    public ResponseEntity<CartResponse> processPayment(
            @RequestBody @Valid CreatePaymentDto createPaymentDto,
            @AuthenticationPrincipal User user
    ) {
        CartResponse cartResponse = paymentService.processPayment(createPaymentDto, user.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(cartResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);
    }
}
