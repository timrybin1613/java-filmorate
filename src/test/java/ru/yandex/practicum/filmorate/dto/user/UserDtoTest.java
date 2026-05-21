package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        dto.setId(1L);
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
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("asd asd");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenEmailInvalidFormat() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("testmail.com");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("");
        dto.setBirthday(LocalDate.of(1980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenBirthdayIsNull() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("test@mail.com");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        UserCreateDto dto = new UserCreateDto();
        dto.setLogin("user");
        dto.setEmail("test@mail.com");
        dto.setBirthday(LocalDate.of(2980, 1, 1));

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldFailWhenIdIsNull() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setLogin("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void shouldFailWhenIdIsNegative() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(-1L);
        dto.setLogin("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void shouldFailWhenLoginContainsSpacesOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setLogin("us er");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenEmailInvalidOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setEmail("user");
        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenBirthdayInFutureOnUpdate() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(1L);
        dto.setBirthday(LocalDate.of(2980, 1, 1));

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}
