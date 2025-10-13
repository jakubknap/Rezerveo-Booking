package pl.rezerveo.booking.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.authentication.dto.request.ConfirmResetPasswordRequest;
import pl.rezerveo.booking.authentication.dto.request.ResetPasswordRequest;
import pl.rezerveo.booking.authentication.service.PasswordService;
import pl.rezerveo.booking.exception.dto.BaseApiValidationError;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.CustomValidationException;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.token.repository.TokenRepository;
import pl.rezerveo.booking.token.service.TokenService;
import pl.rezerveo.booking.token.service.TokenServiceFactory;
import pl.rezerveo.booking.user.model.User;
import pl.rezerveo.booking.user.repository.UserRepository;
import pl.rezerveo.booking.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00000;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.S00000;
import static pl.rezerveo.booking.token.enumerated.TokenType.PASSWORD_RESET_TOKEN;
import static pl.rezerveo.booking.util.MaskingUtil.maskEmail;
import static pl.rezerveo.booking.util.StringUtil.notEquals;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserService userService;
    private final TokenServiceFactory tokenServiceFactory;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public BaseResponse resetPasswordRequest(ResetPasswordRequest request) {
        User user;

        try {
            user = userService.getUserByEmail(request.email());
        } catch (ServiceException ex) {
            log.warn("Password reset requested for non-existing account [{}]. Responding with generic message for security reasons.", maskEmail(request.email()));
            return new BaseResponse(S00000);
        }

        sendPasswordResetLink(user);

        log.info("Successfully send reset password link to user with UUID: [{}]", user.getUuid());
        return new BaseResponse(S00000);
    }

    @Override
    public BaseResponse confirmResetPassword(ConfirmResetPasswordRequest request) {
        Token tokenEntity = tokenServiceFactory.getTokenService(PASSWORD_RESET_TOKEN).getAndValidateToken(request.token().toString());

        validateResetPasswordRequest(request.password(), request.passwordRepeat(), tokenEntity.getUser());

        updatePassword(tokenEntity, request.password());

        log.info("Successfully reset password for user with UUID: [{}]", tokenEntity.getUser().getUuid());
        return new BaseResponse(S00000);
    }

    private void sendPasswordResetLink(User user) {
        TokenService tokenService = tokenServiceFactory.getTokenService(PASSWORD_RESET_TOKEN);

        tokenService.revokeAllUserTokensByType(user.getUuid());

        String passwordResetToken = tokenService.generateToken(user);

//    TODO
    }

    private void validateResetPasswordRequest(String password, String passwordRepeat, User user) {
        if (notEquals(password, passwordRepeat)) {
            log.error("Password does not match password repeated");
            throw new CustomValidationException(E00000, List.of(new BaseApiValidationError("passwordRepeat", "passwords are not the same")));
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            log.error("The password is the same as the user's current password");
            throw new CustomValidationException(E00000, List.of(new BaseApiValidationError("password", "password cannot be the same as the current one")));
        }
    }

    private void updatePassword(Token tokenEntity, String newPassword) {
        User user = tokenEntity.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenEntity.setUsedAt(LocalDateTime.now());
        tokenRepository.save(tokenEntity);
    }
}