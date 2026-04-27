package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreFilmCreateDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaUpdateDto;
import ru.yandex.practicum.filmorate.validation.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmUpdateDto {

    @NotNull
    @Positive
    private Long id;

    private String name;

    @Size(min = 1, max = 200)
    private String description;

    @MinReleaseDate
    private LocalDate releaseDate;

    private MpaUpdateDto mpa;

    private Set<GenreFilmCreateDto> genres;

    @Positive
    private Double duration;
}
