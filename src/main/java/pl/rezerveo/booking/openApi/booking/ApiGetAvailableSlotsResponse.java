package pl.rezerveo.booking.openApi.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.rezerveo.booking.common.dto.PageResponse;
import pl.rezerveo.booking.exception.dto.response.BaseResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Pobierz dostępne sloty do rezerwacji",
        description = "Zwraca listę wszystkich dostępnych slotów, które użytkownik może zarezerwować",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pomyślnie zwrócono dostępne sloty",
                        content = @Content(
                                schema = @Schema(implementation = PageResponse.class),
                                examples = @ExampleObject(
                                        value = """
                                                {
                                                    "content": [
                                                        {
                                                            "uuid": "b05b23d1-f661-4bef-ba3e-63565c8bb217",
                                                            "date": "2025-10-15",
                                                            "startTime": "19:55:00",
                                                            "endTime": "21:55:00",
                                                            "serviceType": "Wymiana oleju silnikowego",
                                                            "mechanicName": "John Doe"
                                                        }
                                                    ],
                                                    "pageable": {
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "sort": {
                                                            "empty": false,
                                                            "sorted": true,
                                                            "unsorted": false
                                                        },
                                                        "offset": 0,
                                                        "paged": true,
                                                        "unpaged": false
                                                    },
                                                    "last": true,
                                                    "totalElements": 1,
                                                    "totalPages": 1,
                                                    "size": 10,
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": false,
                                                        "sorted": true,
                                                        "unsorted": false
                                                    },
                                                    "first": true,
                                                    "numberOfElements": 1,
                                                    "empty": false
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
public @interface ApiGetAvailableSlotsResponse {}