package pl.rezerveo.booking.token.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.authentication.dto.request.ResendTokenRequest;
import pl.rezerveo.booking.event.MailEvent;
import pl.rezerveo.booking.event.service.RabbitEventPublisher;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.properties.TokenProperties;
import pl.rezerveo.booking.security.encryption.EncryptionService;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.token.repository.TokenRepository;
import pl.rezerveo.booking.user.enumerated.UserStatus;
import pl.rezerveo.booking.user.model.User;

import java.time.Duration;
import java.util.UUID;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E03003;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000;
import static pl.rezerveo.booking.event.MailType.ACCOUNT_ACTIVATION;
import static pl.rezerveo.booking.user.enumerated.UserStatus.REGISTERED;

@Slf4j
@Service
public class ActivationAccountTokenServiceImpl extends BaseTokenServiceImpl {

    private final TokenProperties tokenProperties;
    private final RabbitEventPublisher eventPublisher;
    private final EncryptionService encryptionService;

    public ActivationAccountTokenServiceImpl(TokenRepository tokenRepository, TokenProperties tokenProperties,
                                             RabbitEventPublisher eventPublisher, EncryptionService encryptionService) {
        super(tokenRepository);
        this.tokenProperties = tokenProperties;
        this.eventPublisher = eventPublisher;
        this.encryptionService = encryptionService;
    }

    @Override
    protected TokenType getTokenType() {
        return TokenType.ACCOUNT_ACTIVATION_TOKEN;
    }

    @Override
    protected Duration getTokenExpiration() {
        return tokenProperties.getAccountActivationTokenExpiration();
    }

    @Override
    @Transactional
    public BaseResponse resendToken(ResendTokenRequest request) {
        User user = validateTokenAndGetUser(request);

        validateUserStatus(user.getStatus(), user.getUuid());

        revokeAllUserTokensByType(user.getUuid());

        String activationToken = generateToken(user);
        eventPublisher.sendMailEvent(new MailEvent(user.getEmail(), user.getFirstName(), user.getLastName(), activationToken, ACCOUNT_ACTIVATION, encryptionService));

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

    private void validateUserStatus(UserStatus userStatus, UUID userUuid) {
        if (REGISTERED != userStatus) {
            log.error("It is not possible to send a re-verification of the account because the user has already passed the verification before. User with UUID: [{}]", userUuid);
            throw new ServiceException(E03003);
        }
    }
}