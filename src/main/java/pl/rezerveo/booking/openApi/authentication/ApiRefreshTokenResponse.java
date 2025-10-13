package pl.rezerveo.booking.openApi.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.rezerveo.booking.authentication.dto.response.AuthenticationResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Odnowienie Access Tokenu JWT",
        description = "Zwraca nowy token JWT na podstawie refresh tokena",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pomyślnie odświeżono token",
                        content = @Content(
                                schema = @Schema(implementation = AuthenticationResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQHJlemVydmVvLnBsIiwiaWF0IjoxNzYwMzc5MjA2LCJleHAiOjE3NjAzODEwMDZ9.vPLSk34iaMw45KwQbQoqwv1TvLHtVmilBsAvZb82INXImO57sXRCrdwCENuXsdWf",
                                                    "refreshToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQHJlemVydmVvLnBsIiwiaWF0IjoxNzYwMzc5MjA0LCJleHAiOjE3NjA0NjU2MDR9.O6RI8t0xiWoHy21IniXMcrXC7kpLWU9NebTg5_zCXD8OEAIQs5oEac5XD8-ErTLz"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Brak autoryzacji (np. błędny access token)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E01001",
                                                    "message": "Expired token",
                                                    "httpStatus": "UNAUTHORIZED",
                                                    "traceId": "b4bdb452-cf63-41a1-9846-d2c6a3d29e91"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Użytkownik zapisany w tokenie nie istnieje",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E03001",
                                                    "message": "User not found",
                                                    "httpStatus": "NOT_FOUND",
                                                    "traceId": "8302e82a-d1ac-40b7-85df-77ddfef995eb"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Wystąpił wewnętrzny błąd serwera",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E00006",
                                                    "message": "Internal server error",
                                                    "httpStatus": "INTERNAL_SERVER_ERROR",
                                                    "traceId": "2a7d17a1-c10d-49cb-a640-83185a19db6d"
                                                }
                                                """
                                )
                        )
                )
        }
)
public @interface ApiRefreshTokenResponse {
}