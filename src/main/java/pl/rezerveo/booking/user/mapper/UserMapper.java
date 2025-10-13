package pl.rezerveo.booking.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.rezerveo.booking.authentication.dto.request.RegisterRequest;
import pl.rezerveo.booking.user.dto.response.UserDetailsResponse;
import pl.rezerveo.booking.user.model.User;

import static java.util.UUID.randomUUID;
import static pl.rezerveo.booking.user.enumerated.UserStatus.REGISTERED;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User mapToUserEntity(RegisterRequest request) {
        return User.builder()
                   .uuid(randomUUID())
                   .firstName(request.firstName())
                   .lastName(request.lastName())
                   .email(request.email())
                   .password(passwordEncoder.encode(request.password()))
                   .status(REGISTERED)
                   .role(request.role())
                   .build();
    }

    public static UserDetailsResponse buildUserDetailsResponse(User user) {
        return new UserDetailsResponse(user.getUuid(),
                                       user.getFirstName(),
                                       user.getLastName(),
                                       user.getEmail(),
                                       user.getRole());
    }
}