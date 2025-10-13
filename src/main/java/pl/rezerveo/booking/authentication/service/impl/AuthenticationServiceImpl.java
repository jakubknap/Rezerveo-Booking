package pl.rezerveo.booking.authentication.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rezerveo.booking.authentication.dto.request.LoginRequest;
import pl.rezerveo.booking.authentication.dto.request.RegisterRequest;
import pl.rezerveo.booking.authentication.dto.response.AuthenticationResponse;
import pl.rezerveo.booking.authentication.service.AuthenticationService;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.exception.exception.ServiceException;
import pl.rezerveo.booking.security.JwtService;
import pl.rezerveo.booking.security.encryption.EncryptionService;
import pl.rezerveo.booking.token.dto.TokenPairDto;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.token.repository.TokenRepository;
import pl.rezerveo.booking.token.service.TokenServiceFactory;
import pl.rezerveo.booking.user.mapper.UserMapper;
import pl.rezerveo.booking.user.model.User;
import pl.rezerveo.booking.user.repository.UserRepository;
import pl.rezerveo.booking.user.service.UserService;
import pl.rezerveo.booking.util.MaskingUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.*;
import static pl.rezerveo.booking.token.enumerated.TokenType.*;
import static pl.rezerveo.booking.user.enumerated.UserStatus.ACTIVE;
import static pl.rezerveo.booking.user.enumerated.UserStatus.REGISTERED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenServiceFactory tokenServiceFactory;
    private final EncryptionService encryptionService;

    @Override
    @Transactional
    public BaseResponse register(RegisterRequest request) {
        String email = request.email();

        if (userRepository.existsByEmail(encryptionService.encrypt(email))) {
            log.error("User with this e-mail already exists");
            throw new ServiceException(E03000);
        }

        User user = userMapper.mapToUserEntity(request);
        userRepository.save(user);

        sendActivationLink(user);

        log.info("User with e-mail: [{}] has been successfully registered. Assigned UUID: {}", MaskingUtil.maskEmail(email), user.getUuid());
        return new BaseResponse(S00003);
    }

    @Override
    @Transactional
    public BaseResponse activateAccount(UUID token) {
        Token tokenEntity = tokenServiceFactory.getTokenService(ACCOUNT_ACTIVATION_TOKEN).getAndValidateToken(token.toString());

        activateUser(tokenEntity);

        log.info("Successfully activated user with UUID: [{}]", tokenEntity.getUser().getUuid());
        return new BaseResponse(S00000);
    }

    @Override
    @Transactional
    public AuthenticationResponse authenticate(LoginRequest request) {
        User user = tryAuthenticateUser(request);

        TokenPairDto generatedTokens = generateTokens(user);

        log.info("Successfully authenticated user with UUID: [{}]", user.getUuid());
        return new AuthenticationResponse(generatedTokens.accessToken(), generatedTokens.refreshToken());
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String refreshToken = jwtService.readTokenFromHeader(request);

        final User user = extractUserFromRefreshToken(refreshToken);
        final String userEmail = user.getEmail();

        jwtService.validateToken(refreshToken, userEmail, REFRESH_TOKEN);
        jwtService.revokeAllUserJwtAccessTokens(user.getUuid());

        Token accessToken = jwtService.buildJwtTokenEntity(user, jwtService.generateAccessToken(userEmail), ACCESS_TOKEN);
        tokenRepository.save(accessToken);

        log.info("Successfully refreshed access token for user with UUID: [{}]", user.getUuid());
        return new AuthenticationResponse(accessToken.getToken(), refreshToken);
    }

    private void sendActivationLink(User user) {
        String activationToken = tokenServiceFactory.getTokenService(ACCOUNT_ACTIVATION_TOKEN)
                .generateToken(user);
//    TODO
    }

    private void activateUser(Token tokenEntity) {
        User user = tokenEntity.getUser();

        validateUserStatus(user);

        user.setStatus(ACTIVE);
        userRepository.save(user);

        tokenEntity.setUsedAt(LocalDateTime.now());
        tokenRepository.save(tokenEntity);
    }

    private void validateUserStatus(User user) {
        if (REGISTERED != user.getStatus()) {
            log.error("The user with UUID: [{}] has already been activated before. Re-verification is not possible.", user.getUuid());
            throw new ServiceException(E03003);
        }
    }

    private User tryAuthenticateUser(LoginRequest request) {
        String email = request.email();
        User user = userService.getUserByEmail(email);

        if (REGISTERED == user.getStatus()) {
            log.error("User with UUID: [{}] does not have an active account. Cannot authenticate", user.getUuid());
            throw new ServiceException(E03002);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (BadCredentialsException ex) {
            log.error("Bad credentials: {}", ex.getMessage(), ex);
            throw new ServiceException(E02000);

        } catch (UsernameNotFoundException ex) {
            log.error("User not found: {}", ex.getMessage(), ex);
            throw new ServiceException(E03001);

        } catch (DisabledException ex) {
            log.error("User is disabled: {}", ex.getMessage(), ex);
            throw new ServiceException(E02001);

        } catch (LockedException ex) {
            log.error("User is locked: {}", ex.getMessage(), ex);
            throw new ServiceException(E02002);

        } catch (AccountExpiredException ex) {
            log.error("Account expired: {}", ex.getMessage(), ex);
            throw new ServiceException(E02003);

        } catch (CredentialsExpiredException ex) {
            log.error("Credentials expired: {}", ex.getMessage(), ex);
            throw new ServiceException(E02004);

        } catch (Exception ex) {
            log.error("Unexpected error during authentication: {}", ex.getMessage(), ex);
            throw new ServiceException(E00006);
        }

        return user;
    }

    private TokenPairDto generateTokens(User user) {
        String email = user.getEmail();

        jwtService.revokeAllUserJwtTokens(user.getUuid());

        Token accessToken = jwtService.buildJwtTokenEntity(user, jwtService.generateAccessToken(email), ACCESS_TOKEN);
        Token refreshToken = jwtService.buildJwtTokenEntity(user, jwtService.generateRefreshToken(email), REFRESH_TOKEN);

        tokenRepository.saveAll(List.of(accessToken, refreshToken));
        return new TokenPairDto(accessToken.getToken(), refreshToken.getToken());
    }

    private User extractUserFromRefreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsernameFromToken(refreshToken);
        return userService.getUserByEmail(userEmail);
    }
}