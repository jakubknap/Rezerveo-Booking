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
        summary = "Logowanie użytkownika",
        description = "Pozwala zalogować użytkownika i zwraca tokeny JWT",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Użytkownik pomyślnie zalogowany",
                        content = @Content(
                                schema = @Schema(implementation = AuthenticationResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQHJlemVydmVvLnBsIiwiaWF0IjoxNzYwMzc4MzE1LCJleHAiOjE3NjAzODAxMTV9.17QLBU-MCWVW0J23RIt3tuLQovL6l29WFMp7suY6xnxpYXJQPrRuFyBLWppMKLod",
                                                    "refreshToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQHJlemVydmVvLnBsIiwiaWF0IjoxNzYwMzc4MzE1LCJleHAiOjE3NjA0NjQ3MTV9.vtTBadWf5hlCY0e21u9Hf5OCQs4gWkrKBX7nQLAanAJO7ZpwREZE76_A-EmwGzIp"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Niepoprawne dane wejściowe (np. błędny e-mail, zbyt krótkie hasło)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E00000",
                                                    "message": "Bad Request",
                                                    "httpStatus": "BAD_REQUEST",
                                                    "traceId": "fa74267a-955f-4469-88d4-60ed599e8ca7",
                                                    "errors": [
                                                        {
                                                            "field": "email",
                                                            "message": "nie może być odstępem"
                                                        }
                                                    ]
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Użytkownik podał błędne dane lub konto nie jest aktywne",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E02000",
                                                    "message": "Incorrect e-mail or password",
                                                    "httpStatus": "UNAUTHORIZED",
                                                    "traceId": "701b1450-2d71-445d-a8d5-f72767cdde8f"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Użytkownik o podanym adresie e-mail nie istnieje",
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
                        responseCode = "422",
                        description = "Użytkownik nie ma aktywowanego konta",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E03002",
                                                    "message": "User does not have an active account",
                                                    "httpStatus": "UNPROCESSABLE_ENTITY",
                                                    "traceId": "f173822a-9acb-490e-ace3-ae9dfc604b4b"
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
public @interface ApiAuthenticationResponse {
}