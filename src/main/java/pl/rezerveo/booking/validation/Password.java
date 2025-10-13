package pl.rezerveo.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static pl.rezerveo.booking.common.constant.Patterns.PASSWORD_PATTERN;

@Documented
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)

@NotBlank
@Size(min = 8, max = 100)
@Pattern(regexp = PASSWORD_PATTERN)
public @interface Password {

    String message() default "{}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}