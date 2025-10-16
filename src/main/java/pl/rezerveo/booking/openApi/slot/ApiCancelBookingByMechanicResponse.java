package pl.rezerveo.booking.openApi.slot;

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
        summary = "Anuluj rezerwację przez mechanika",
        description = "Pozwala mechanikowi anulować rezerwację w swoim slocie. "
                      + "Jeżeli po anulowaniu nie pozostaną inne potwierdzone rezerwacje w slocie, jego status zostanie zmieniony na AVAILABLE.",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Rezerwacja została pomyślnie anulowana przez mechanika",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "S00001",
                                                    "message": "Success",
                                                    "httpStatus": "NO_CONTENT"
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
                        responseCode = "403",
                        description = "Mechanik nie jest właścicielem slotu, w którym znajduje się rezerwacja",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E05002",
                                                    "message": "Logged in user is not the owner of the slot",
                                                    "httpStatus": "FORBIDDEN",
                                                    "traceId": "92d189fc-e37c-4877-a993-b1eff94456e3"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Rezerwacja lub slot o podanym UUID nie istnieje",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E06000",
                                                    "message": "Booking not found",
                                                    "httpStatus": "NOT_FOUND",
                                                    "traceId": "504f1d1f-0abc-44cc-a494-30069dc30015"
                                                }
                                                """
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Rezerwacja nie może zostać anulowana (np. już została wcześniej anulowana, lub się odbyła)",
                        content = @Content(
                                schema = @Schema(implementation = BaseResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "status": "E06001",
                                                    "message": "Booking status does not allow cancellation",
                                                    "httpStatus": "UNPROCESSABLE_ENTITY",
                                                    "traceId": "5524439f-dba5-446b-ba4f-c653740eb2d0"
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
public @interface ApiCancelBookingByMechanicResponse {}