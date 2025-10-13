package pl.rezerveo.booking.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.service.impl.ActivationAccountTokenServiceImpl;
import pl.rezerveo.booking.token.service.impl.ResetPasswordTokenServiceImpl;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00005;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServiceFactory {

    private final ActivationAccountTokenServiceImpl activationAccountTokenService;
    private final ResetPasswordTokenServiceImpl resetPasswordTokenService;

    public TokenService getTokenService(TokenType tokenType) {
        return switch (tokenType) {
            case ACCOUNT_ACTIVATION_TOKEN -> activationAccountTokenService;
            case PASSWORD_RESET_TOKEN -> resetPasswordTokenService;
            default -> {
                log.error("Unsupported token type: [{}]", tokenType);
                throw new ServiceException(E00005);
            }
        };
    }
}