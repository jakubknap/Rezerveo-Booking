package pl.rezerveo.booking.token.dto;

import pl.rezerveo.booking.token.enumerated.TokenType;

public record TokenStatusAndType(boolean isRevoked, TokenType tokenType) {
}