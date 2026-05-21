package ru.yandex.practicum.filmorate.dto.genre;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.film.Genre;

@Data
public class GenreResponseDto {
    private Long id;
    private String name;

    public GenreResponseDto(Genre genre) {
        this.id = genre.getId();
        this.name = genre.getName();
    }
}
