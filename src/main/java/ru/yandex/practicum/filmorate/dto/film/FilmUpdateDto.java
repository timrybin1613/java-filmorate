package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotation.MinReleaseDate;

import java.time.LocalDate;

@Data
public class FilmUpdateDto {

    @NotNull
    @Positive
    private Integer id;

    private String name;

    @Size(min = 1, max = 200)
    private String description;

    @MinReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Double duration;
}
