package pl.rezerveo.booking.exception.dto;

public record BaseApiValidationError(String field, String message) {
}