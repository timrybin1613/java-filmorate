package ru.yandex.practicum.filmorate.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validation.annotation.NoSpacesLogin;

public class NoSpacesLoginValidator implements ConstraintValidator<NoSpacesLogin, String> {

    @Override
    public void initialize(NoSpacesLogin constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return s.chars().noneMatch(Character::isWhitespace);
    }
}
