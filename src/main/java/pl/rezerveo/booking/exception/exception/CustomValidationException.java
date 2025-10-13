package pl.rezerveo.booking.exception.exception;

import lombok.Getter;
import pl.rezerveo.booking.common.enumerated.ResponseCode;
import pl.rezerveo.booking.exception.dto.BaseApiValidationError;

import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {

    private final ResponseCode status;
    private final List<BaseApiValidationError> errors;

    public CustomValidationException(ResponseCode status, List<BaseApiValidationError> errors) {
        super(status.getMessage());
        this.status = status;
        this.errors = errors;
    }
}