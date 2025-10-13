package pl.rezerveo.booking.security.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.user.model.User;

import java.util.UUID;

import static java.util.Objects.isNull;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00006;

@Slf4j
@UtilityClass
public class SecurityUtils {

    public static User getLoggedUser() {
        final Authentication authentication = getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            log.error("No authenticated user found");
            throw new ServiceException(E00006);
        }

        final Object principal = authentication.getPrincipal();

        if (isNull(principal)) {
            log.error("Authentication principal is null");
            throw new ServiceException(E00006);
        }

        if (principal instanceof User) {
            return (User) principal;
        }

        log.error("Principal is not instance of User");
        throw new ServiceException(E00006);
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static UUID getLoggedUserUUID() {
        return getLoggedUser().getUuid();
    }
}