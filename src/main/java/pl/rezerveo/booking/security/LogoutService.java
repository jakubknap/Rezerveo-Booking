package pl.rezerveo.booking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.common.enumerated.ResponseCode;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;
import pl.rezerveo.booking.token.repository.TokenRepository;

import java.io.IOException;
import java.util.UUID;

import static java.util.Objects.isNull;
import static pl.rezerveo.booking.common.enumerated.ResponseCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("Logout process started");
        try {
            final String token = jwtService.readTokenFromHeader(request);

            if (isNull(token)) {
                log.error("No authorization header or invalid authorization header");
                writeResponse(response, E01000);
                return;
            }

            final UUID userUuidFromToken = tokenRepository.findUserUuidByToken(token).orElse(null);

            if (isNull(userUuidFromToken)) {
                log.error("Token not found in database");
                writeResponse(response, E01003);
                return;
            }

            jwtService.revokeAllUserJwtTokens(userUuidFromToken);
            SecurityContextHolder.clearContext();

            log.info("Logout successful for user with UUID: [{}]", userUuidFromToken);
            writeResponse(response, S00000);
        } catch (Exception ex) {
            log.error("Logout failed: {}", ex.getMessage(), ex);
            writeResponse(response, E00006);
        }
    }

    private void writeResponse(HttpServletResponse response, ResponseCode responseCode) {
        try {
            response.setStatus(responseCode.getHttpStatus().value());
            objectMapper.writeValue(response.getOutputStream(), new BaseResponse(responseCode));
        } catch (IOException ex) {
            log.error("Failed to write response: {}", ex.getMessage(), ex);
        }
    }
}