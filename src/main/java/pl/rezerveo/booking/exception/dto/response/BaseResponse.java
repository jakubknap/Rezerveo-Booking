package pl.rezerveo.booking.exception.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.rezerveo.booking.common.enumerated.ResponseCode;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.UUID.randomUUID;

@Slf4j
@Getter
@JsonInclude(NON_NULL)
public class BaseResponse {

    private final ResponseCode status;
    private final String message;
    private final HttpStatus httpStatus;
    private final UUID traceId;

    public BaseResponse(ResponseCode status) {
        this.status = status;
        this.message = status.getMessage();
        this.httpStatus = status.getHttpStatus();
        this.traceId = this.httpStatus.isError() ? generateTraceId() : null;
    }

    public BaseResponse(ResponseCode status, String message) {
        this.status = status;
        this.message = message;
        this.httpStatus = status.getHttpStatus();
        this.traceId = this.httpStatus.isError() ? generateTraceId() : null;
    }

    private UUID generateTraceId() {
        final UUID traceId = randomUUID();
        log.error("ERROR - {} traceId: {} message: {}", this.status, traceId, this.message);
        return traceId;
    }

    public ResponseEntity<BaseResponse> get() {
        return new ResponseEntity<>(this, this.status.getHttpStatus());
    }
}