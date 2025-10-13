package pl.rezerveo.booking.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import pl.rezerveo.booking.authentication.dto.request.LoginRequest;
import pl.rezerveo.booking.authentication.dto.request.RegisterRequest;
import pl.rezerveo.booking.authentication.dto.response.AuthenticationResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.util.UUID;

public interface AuthenticationService {

    BaseResponse register(RegisterRequest request);

    BaseResponse activateAccount(UUID token);

    AuthenticationResponse authenticate(LoginRequest request);

    AuthenticationResponse refreshToken(HttpServletRequest request);
}