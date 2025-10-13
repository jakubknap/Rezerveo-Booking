package pl.rezerveo.booking.authentication.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.rezerveo.booking.token.enumerated.TokenType;

import java.util.UUID;

public record ResendTokenRequest(

        @NotNull
        TokenType tokenType,

        @NotNull
        UUID expiredToken
) {
}