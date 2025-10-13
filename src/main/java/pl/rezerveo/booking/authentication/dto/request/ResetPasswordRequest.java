package pl.rezerveo.booking.authentication.dto.request;

import pl.rezerveo.booking.validation.Email;

public record ResetPasswordRequest(

        @Email
        String email
) {
}