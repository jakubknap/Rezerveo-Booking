package pl.rezerveo.booking.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import pl.rezerveo.booking.validation.Email;

public record LoginRequest(

        @Email
        String email,

        @NotBlank
        String password
) {
}