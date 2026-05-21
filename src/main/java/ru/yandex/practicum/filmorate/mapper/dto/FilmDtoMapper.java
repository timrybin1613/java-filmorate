package ru.yandex.practicum.filmorate.mapper.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.*;

@Component
public class FilmDtoMapper {

    public Film toFilm(FilmCreateDto dto) {
        Film film = new Film();

        Set<Long> genres = new HashSet<>();
        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            dto.getGenres().forEach(genreDto -> {
                genres.add(genreDto.getId());
            });
        }
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpa(dto.getMpa().getId());
        film.setGenres(genres);

        return film;
    }

    public FilmResponseDto toFilmResponse(Film film, Mpa mpa, List<Genre> genres) {
        FilmResponseDto filmResponseDto = new FilmResponseDto();
        MpaResponseDto mpaResponseDto = new MpaResponseDto();
        List<GenreResponseDto> genresResponseDto = new ArrayList<>();

        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> {
                genresResponseDto.add(new GenreResponseDto(genre));
            });
        }

        if (mpa != null) {
            mpaResponseDto.setId(mpa.getId());
            mpaResponseDto.setName(mpa.getName());
        }

        filmResponseDto.setMpa(mpaResponseDto);
        filmResponseDto.setId(film.getId());
        filmResponseDto.setName(film.getName());
        filmResponseDto.setDescription(film.getDescription());
        filmResponseDto.setReleaseDate(film.getReleaseDate());
        filmResponseDto.setGenres(genresResponseDto);
        filmResponseDto.setDuration(film.getDuration());

        return filmResponseDto;
    }
}
