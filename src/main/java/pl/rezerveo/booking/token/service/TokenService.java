package pl.rezerveo.booking.token.service;

import pl.rezerveo.booking.authentication.dto.request.ResendTokenRequest;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.user.model.User;

import java.util.UUID;

public interface TokenService {

    void revokeAllUserTokensByType(UUID userUuid);

    String generateToken(User user);

    Token getAndValidateToken(String token);

    BaseResponse resendToken(ResendTokenRequest request);
}