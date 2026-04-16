package ru.yandex.practicum.filmorate.mapper.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmMapper {

    public Film toFilm(FilmCreateDto dto) {
        Film film = new Film();

        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        return film;
    }

    public Film toFilm(FilmUpdateDto dto) {
        Film film = new Film();

        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        return film;
    }

    public FilmResponseDto toFilmResponse(Film film) {
        FilmResponseDto filmResponseDto = new FilmResponseDto();

        filmResponseDto.setId(film.getId());
        filmResponseDto.setName(film.getName());
        filmResponseDto.setDescription(film.getDescription());
        filmResponseDto.setReleaseDate(film.getReleaseDate());
        filmResponseDto.setDuration(film.getDuration());

        return filmResponseDto;
    }

}
