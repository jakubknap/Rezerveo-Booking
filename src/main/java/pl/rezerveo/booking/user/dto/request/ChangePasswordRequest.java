package pl.rezerveo.booking.user.dto.request;

import pl.rezerveo.booking.validation.Password;

public record ChangePasswordRequest(

        @Password
        String password,

        @Password
        String passwordRepeat
) {
}