package pl.rezerveo.booking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.rezerveo.booking.common.enumerated.ResponseCode;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.io.IOException;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E01000;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        final ResponseCode responseCode = E01000;
        response.setStatus(responseCode.getHttpStatus().value());
        objectMapper.writeValue(response.getOutputStream(), new BaseResponse(responseCode));
    }
}