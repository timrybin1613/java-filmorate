package ru.yandex.practicum.filmorate.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validation.validator.MinReleaseDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinReleaseDateValidator.class)
public @interface MinReleaseDate {
    String message() default "слишком ранняя дата релиза";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
