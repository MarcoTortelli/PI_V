package dev.bruno.ecommerce.exception;

public class ActiveCartAlreadyExistsException extends RuntimeException {
    public ActiveCartAlreadyExistsException(String message) {
        super(message);
    }
}