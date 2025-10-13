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
        summary = "Potwierdzenie resetu hasła",
        description = "Pozwala ustawić nowe hasło na podstawie tokenu resetu",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pomyślnie zmieniono hasło",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "S00000",
                                                    "message": "Success",
                                                    "httpStatus": "OK"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Niepoprawne dane wejściowe (np. błędne hasło lub token)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E00000",
                                                    "message": "Bad Request",
                                                    "httpStatus": "BAD_REQUEST",
                                                    "traceId": "99a61bbf-6a7a-445d-990e-0b15d955a107",
                                                    "errors": [
                                                        {
                                                            "field": "passwordRepeat",
                                                            "message": "passwords are not the same"
                                                        }
                                                    ]
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Nie znaleziono tokenu",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E04000",
                                                    "message": "Token not found",
                                                    "httpStatus": "NOT_FOUND",
                                                    "traceId": "01988a20-dc18-4792-924e-eed04eaa47a9"
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
public @interface ApiResetPasswordConfirmResponse {
}