package pl.rezerveo.booking.authentication.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.rezerveo.booking.user.enumerated.Role;
import pl.rezerveo.booking.validation.Email;
import pl.rezerveo.booking.validation.FirstName;
import pl.rezerveo.booking.validation.LastName;
import pl.rezerveo.booking.validation.Password;

public record RegisterRequest(

        @FirstName
        String firstName,

        @LastName
        String lastName,

        @Email
        String email,

        String phoneNumber,

        @Password
        String password,

        @NotNull
        Role role
) {}