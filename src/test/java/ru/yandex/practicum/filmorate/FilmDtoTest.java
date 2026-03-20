package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassWhenValidCreate() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);
        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenValidUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);
        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("");
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldPassWhenDescription200() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("a".repeat(200));
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("a".repeat(201));
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailWhenReleaseDateIsNull() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailWhenReleaseDateTooEarly() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1895, 1, 1));
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailWhenDurationIsNull() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1995, 1, 1));

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1995, 1, 1));
        dto.setDuration(-1D);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldPassWhenDurationIsPositiveButSmall() {
        FilmCreateDto dto = new FilmCreateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1995, 1, 1));
        dto.setDuration(0.1);

        Set<ConstraintViolation<FilmCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenIdIsNull() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1995, 1, 1));
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }


    @Test
    void shouldPassWhenDescription200ForUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);
        dto.setName("film");
        dto.setDescription("a".repeat(200));
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenDescriptionTooLongForUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);
        dto.setName("film");
        dto.setDescription("a".repeat(201));
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldPassWhenDescriptionEmptyForUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);
        dto.setName("film");
        dto.setReleaseDate(LocalDate.now());
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenReleaseDateTooEarlyForUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);
        dto.setName("film");
        dto.setDescription("film");
        dto.setReleaseDate(LocalDate.of(1895, 1, 1));
        dto.setDuration(1D);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldPassWhenNotRequiredFieldsNotPresentForUpdate() {
        FilmUpdateDto dto = new FilmUpdateDto();
        dto.setId(1);

        Set<ConstraintViolation<FilmUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
