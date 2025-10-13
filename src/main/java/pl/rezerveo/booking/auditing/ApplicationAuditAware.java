package pl.rezerveo.booking.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import pl.rezerveo.booking.security.util.SecurityUtils;
import pl.rezerveo.booking.user.model.User;

import java.util.Optional;

import static java.util.Objects.isNull;

public class ApplicationAuditAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityUtils.getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        User user = (User) authentication.getPrincipal();
        return Optional.ofNullable(user.getId());
    }
}