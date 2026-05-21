package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;

import java.time.LocalDate;
import java.util.List;


@Data
public class FilmResponseDto {

    private long id;
    private String name;
    private String description;
    private List<GenreResponseDto> genres;
    private MpaResponseDto mpa;
    private LocalDate releaseDate;
    private Double duration;

}
