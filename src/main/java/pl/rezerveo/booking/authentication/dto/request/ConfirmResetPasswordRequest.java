package pl.rezerveo.booking.authentication.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.rezerveo.booking.validation.Password;

import java.util.UUID;

public record ConfirmResetPasswordRequest(

        @NotNull
        UUID token,

        @Password
        String password,

        @Password
        String passwordRepeat
) {
}