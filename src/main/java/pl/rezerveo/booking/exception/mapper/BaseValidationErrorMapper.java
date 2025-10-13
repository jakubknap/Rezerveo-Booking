package pl.rezerveo.booking.exception.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import pl.rezerveo.booking.exception.dto.BaseApiValidationError;

import java.util.List;

@UtilityClass
public class BaseValidationErrorMapper {

    private static final String FIELD = "field";

    public static List<BaseApiValidationError> mapBindingResult(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(BaseValidationErrorMapper::mapToApiValidationError)
                .distinct()
                .toList();
    }

    public static List<BaseApiValidationError> mapCustomValidationErrors(List<BaseApiValidationError> errors) {
        return errors.stream()
                .distinct()
                .toList();
    }

    private static BaseApiValidationError mapToApiValidationError(ObjectError error) {
        final String defaultMessage = error.getDefaultMessage();

        String field = FIELD;

        if (error instanceof FieldError fieldError) {
            field = fieldError.getField();
        }

        return new BaseApiValidationError(field, defaultMessage);
    }
}