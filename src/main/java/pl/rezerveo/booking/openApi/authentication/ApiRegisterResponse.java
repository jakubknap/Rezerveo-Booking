package pl.rezerveo.booking.openApi.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Rejestracja nowego użytkownika",
        description = "Pozwala utworzyć nowe konto użytkownika",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Konto utworzone pomyślnie",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "S00003",
                                                    "message": "Success",
                                                    "httpStatus": "CREATED"
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
                        responseCode = "409",
                        description = "Użytkownik o podanym adresie e-mail już istnieje",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E03000",
                                                    "message": "User already exists",
                                                    "httpStatus": "CONFLICT",
                                                    "traceId": "c66f5f3a-5b31-4b65-8065-794525fb79d8"
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
public @interface ApiRegisterResponse {
}