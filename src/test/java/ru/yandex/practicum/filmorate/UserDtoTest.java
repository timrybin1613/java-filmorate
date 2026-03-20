package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassWhenValidCreate() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));
        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1);
        dto.setLogin("user");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(1999, 1, 1));
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")
                && v.getMessage().equals("не должно быть пустым")));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("asd asd");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")
                && v.getMessage().equals("логин содержит пробелы")));
    }

    @Test
    void shouldFailWhenEmailInvalidFormat() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("testmail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")
                && v.getMessage().equals("должно иметь формат адреса электронной почты")));
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")
                && v.getMessage().equals("не должно быть пустым")));
    }

    @Test
    void shouldFailWhenBirthdayIsNull() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("test@mail.com");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")
                && v.getMessage().equals("не должно равняться null")));
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(2980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")
                && v.getMessage().equals("должно содержать прошедшую дату")));
    }

    @Test
    void shouldFailWhenIdIsNull() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setLogin("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")
                && v.getMessage().equals("не должно равняться null")));
    }

    @Test
    void shouldFailWhenIdIsNegative() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(-1);
        dto.setLogin("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")
                && v.getMessage().equals("должно быть больше 0")));
    }

    @Test
    void shouldFailWhenLoginContainsSpacesOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1);
        dto.setLogin("us er");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")
                && v.getMessage().equals("логин содержит пробелы")));
    }

    @Test
    void shouldFailWhenEmailInvalidOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1);
        dto.setEmail("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")
                && v.getMessage().equals("должно иметь формат адреса электронной почты")));
    }

    @Test
    void shouldFailWhenBirthdayInFutureOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1);
        dto.setBirthday(LocalDate.of(2980, 1, 1));

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")
                && v.getMessage().equals("должно содержать прошедшую дату")));
    }
}
