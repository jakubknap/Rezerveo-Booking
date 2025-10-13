package pl.rezerveo.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static pl.rezerveo.booking.common.constant.Patterns.SAVE_TEXT_PATTERN;

@Documented
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)

@Pattern(regexp = SAVE_TEXT_PATTERN, message = "Invalid characters used")
public @interface SafeText {

    String message() default "{}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}