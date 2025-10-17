package pl.rezerveo.booking.token.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.authentication.dto.request.ResendTokenRequest;
import pl.rezerveo.booking.event.MailEvent;
import pl.rezerveo.booking.event.service.RabbitEventPublisher;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.properties.TokenProperties;
import pl.rezerveo.booking.security.encryption.EncryptionService;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.token.repository.TokenRepository;
import pl.rezerveo.booking.user.model.User;

import java.time.Duration;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000;
import static pl.rezerveo.booking.event.MailType.PASSWORD_RESET;

@Slf4j
@Service
public class ResetPasswordTokenServiceImpl extends BaseTokenServiceImpl {

    private final TokenProperties tokenProperties;
    private final RabbitEventPublisher eventPublisher;
    private final EncryptionService encryptionService;

    public ResetPasswordTokenServiceImpl(TokenRepository tokenRepository, TokenProperties tokenProperties,
                                         RabbitEventPublisher eventPublisher, EncryptionService encryptionService) {
        super(tokenRepository);
        this.tokenProperties = tokenProperties;
        this.eventPublisher = eventPublisher;
        this.encryptionService = encryptionService;
    }

    @Override
    protected TokenType getTokenType() {
        return TokenType.PASSWORD_RESET_TOKEN;
    }

    @Override
    protected Duration getTokenExpiration() {
        return tokenProperties.getPasswordResetTokenExpiration();
    }

    @Override
    @Transactional
    public BaseResponse resendToken(ResendTokenRequest request) {
        User user = validateTokenAndGetUser(request);

        revokeAllUserTokensByType(user.getUuid());

        String passwordResetToken = generateToken(user);
        eventPublisher.sendMailEvent(new MailEvent(user.getEmail(), user.getFirstName(), user.getLastName(), passwordResetToken, PASSWORD_RESET, encryptionService));

        log.info("Successfully resent token to user with UUID: [{}]", user.getUuid());
        return new BaseResponse(S00000);
    }

    private User validateTokenAndGetUser(ResendTokenRequest request) {
        String token = request.expiredToken().toString();

        Token tokenEntity = findTokenWithUserOrElseThrow(token);
        validateTokenType(tokenEntity.getTokenType(), token);
        validateTokenStatus(tokenEntity.isRevoked(), token);
        validateTokenUsage(tokenEntity.getUsedAt(), token);

        return tokenEntity.getUser();
    }
}