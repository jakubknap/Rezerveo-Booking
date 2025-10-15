package pl.rezerveo.booking.openApi.booking;

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
        summary = "Zarezerwuj wybrany slot",
        description = "Pozwala użytkownikowi zarezerwować dostępny slot",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Slot został pomyślnie zarezerwowany",
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
                        responseCode = "401",
                        description = "Użytkownik nieuwierzytelniony",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E01002",
                                                    "message": "Invalid token",
                                                    "httpStatus": "UNAUTHORIZED",
                                                    "traceId": "0c501596-9e18-4bc3-b0fc-9ae3dc9e5f31"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Slot o podanym UUID nie istnieje",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E05001",
                                                    "message": "Slot not found",
                                                    "httpStatus": "NOT_FOUND",
                                                    "traceId": "0a684d5f-fc05-481b-8b02-79a011509db2"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Rezerwacja nie może zostać dokonana (np. slot został wcześniej zarezerwowany przez innego użytkownika lub slot został odwołany)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E05004",
                                                    "message": "Slot is no longer available for booking",
                                                    "httpStatus": "UNPROCESSABLE_ENTITY",
                                                    "traceId": "1f90b54e-b212-4e40-a68d-992cb1e1e4f6"
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
public @interface ApiBookSlotResponse {}