package pl.rezerveo.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static pl.rezerveo.booking.common.constant.Patterns.PERSON_NAME_PATTERN;

@Documented
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)

@NotBlank
@Length(min = 2, max = 35)
@Pattern(regexp = PERSON_NAME_PATTERN)
public @interface LastName {

    String message() default "{}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}