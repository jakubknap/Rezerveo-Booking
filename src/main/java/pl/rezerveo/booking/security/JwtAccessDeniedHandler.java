package pl.rezerveo.booking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pl.rezerveo.booking.common.enumerated.ResponseCode;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.io.IOException;

import static pl.rezerveo.booking.common.enumerated.ResponseCode.E00001;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        final ResponseCode responseCode = E00001;
        response.setStatus(responseCode.getHttpStatus().value());
        objectMapper.writeValue(response.getOutputStream(), new BaseResponse(responseCode));
    }
}