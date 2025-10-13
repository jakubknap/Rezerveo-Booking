package pl.rezerveo.booking.user.dto.request;

import pl.rezerveo.booking.validation.Email;
import pl.rezerveo.booking.validation.FirstName;
import pl.rezerveo.booking.validation.LastName;

public record UpdateUserRequest(

        @FirstName
        String firstName,

        @LastName
        String lastName,

        @Email
        String email
) {}