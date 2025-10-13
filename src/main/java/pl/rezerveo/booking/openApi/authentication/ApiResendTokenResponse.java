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
        summary = "Wyślij ponownie token",
        description = "Pozwala wysłać ponownie token np. aktywacyjny lub resetu hasła",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pomyślnie wysłano nowy token",
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
                        description = "Niepoprawne dane wejściowe (np. błędny token)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E04004",
                                                    "message": "Token used",
                                                    "httpStatus": "BAD_REQUEST",
                                                    "traceId": "4c36c625-5bd1-4dac-8528-f9a386d4553f"
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
                        responseCode = "422",
                        description = "Użytkownik miał już aktywowane konto",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E03003",
                                                    "message": "User already had the account activated",
                                                    "httpStatus": "UNPROCESSABLE_ENTITY",
                                                    "traceId": "e2f3cff3-865f-4518-a9ac-230e5f246ddf"
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
public @interface ApiResendTokenResponse {
}