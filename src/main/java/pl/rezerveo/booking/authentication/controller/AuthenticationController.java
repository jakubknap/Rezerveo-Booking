package pl.rezerveo.booking.authentication.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.rezerveo.booking.authentication.dto.request.*;
import pl.rezerveo.booking.authentication.dto.response.AuthenticationResponse;
import pl.rezerveo.booking.authentication.service.AuthenticationService;
import pl.rezerveo.booking.authentication.service.PasswordService;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.openApi.authentication.*;
import pl.rezerveo.booking.token.enumerated.TokenType;
import pl.rezerveo.booking.token.service.TokenServiceFactory;

import java.util.UUID;

import static pl.rezerveo.booking.common.constant.Urls.AUTH_URL;
import static pl.rezerveo.booking.util.MaskingUtil.maskEmail;

@Slf4j
@RestController
@RequestMapping(AUTH_URL)
@RequiredArgsConstructor
@Tag(name = "Dostęp do konta", description = "Operacje związane z uwierzytelnianiem i kontem")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;
    private final TokenServiceFactory tokenServiceFactory;

    @ApiRegisterResponse
    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Start user registration with e-mail: [{}]", maskEmail(request.email()));
        return authenticationService.register(request);
    }

    @ApiActivateAccountResponse
    @GetMapping("/activate-account/{activationToken}")
    public BaseResponse activateAccount(@PathVariable UUID activationToken) {
        log.info("Start user account activation for token: [{}]", activationToken);
        return authenticationService.activateAccount(activationToken);
    }

    @ApiAuthenticationResponse
    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@Valid @RequestBody LoginRequest request) {
        log.info("Start user authentication with e-mail: [{}]", maskEmail(request.email()));
        return authenticationService.authenticate(request);
    }

    @ApiRefreshTokenResponse
    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        log.info("Start refreshing user token");
        return authenticationService.refreshToken(request);
    }

    @ApiResetPasswordRequestResponse
    @PostMapping("/password-reset/request")
    public BaseResponse resetPasswordRequest(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Start password recovery process for user with e-mail: [{}]", maskEmail(request.email()));
        return passwordService.resetPasswordRequest(request);
    }

    @ApiResetPasswordConfirmResponse
    @PostMapping("/password-reset/confirm")
    public BaseResponse confirmResetPassword(@Valid @RequestBody ConfirmResetPasswordRequest request) {
        log.info("Start password reset process for token: [{}]", request.token());
        return passwordService.confirmResetPassword(request);
    }

    @ApiResendTokenResponse
    @PostMapping("/resend-token")
    public BaseResponse resendToken(@Valid @RequestBody ResendTokenRequest request) {
        TokenType tokenType = request.tokenType();
        log.info("Start resending token: [{}] for user with expired token: [{}]", tokenType, request.expiredToken());
        return tokenServiceFactory.getTokenService(tokenType).resendToken(request);
    }
}