package dev.bruno.ecommerce.exception;

public class OrderAlreadyPaidException extends RuntimeException {
    public OrderAlreadyPaidException(String message) {
        super(message);
    }
}
