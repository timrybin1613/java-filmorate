package ru.yandex.practicum.filmorate.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validation.validator.NoSpacesLoginValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoSpacesLoginValidator.class)
public @interface NoSpacesLogin {
    String message() default "логин содержит пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
