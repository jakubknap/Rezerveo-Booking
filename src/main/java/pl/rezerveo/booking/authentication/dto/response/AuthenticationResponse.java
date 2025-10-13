package pl.rezerveo.booking.authentication.dto.response;

public record AuthenticationResponse(String accessToken, String refreshToken) {
}