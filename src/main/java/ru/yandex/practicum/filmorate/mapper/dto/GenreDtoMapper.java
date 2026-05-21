package ru.yandex.practicum.filmorate.mapper.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.model.film.Genre;

@Component
public class GenreDtoMapper {

    public GenreResponseDto toGenreResponse(Genre dto) {
        return new GenreResponseDto(dto);
    }

}
