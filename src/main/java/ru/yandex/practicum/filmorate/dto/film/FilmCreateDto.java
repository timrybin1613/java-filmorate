package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreFilmCreateDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaCreateDto;
import ru.yandex.practicum.filmorate.validation.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmCreateDto {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @MinReleaseDate
    private LocalDate releaseDate;

    private MpaCreateDto mpa;

    private Set<GenreFilmCreateDto> genres;

    @NotNull
    @Positive
    private Double duration;
}
