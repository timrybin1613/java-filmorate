package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<Integer, Film>();
    private int nextId = 0;

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto) {
        log.info("Adding film {}", filmCreateDto);
        Film film = new Film();
        int id = getNextId();
        film.setId(id);
        film.setName(filmCreateDto.getName());
        film.setDescription(filmCreateDto.getDescription());
        film.setReleaseDate(filmCreateDto.getReleaseDate());
        film.setDuration(filmCreateDto.getDuration());

        films.put(id, film);
        log.info("Film added {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody FilmUpdateDto filmUpdateDto) {
        log.info("Updating film {}", filmUpdateDto);
        int updatedFilmId = filmUpdateDto.getId();

        Film updatedFilm = Optional.ofNullable(films.get(updatedFilmId))
                .orElseThrow(() -> new NotFoundException("Film id " + updatedFilmId + " not found"));

        LocalDate updatedReleaseDate = filmUpdateDto.getReleaseDate();

        updatedFilm.setReleaseDate(updatedReleaseDate);

        Double updatedDuration = filmUpdateDto.getDuration();
        String name = filmUpdateDto.getName();
        String description = filmUpdateDto.getDescription();

        if (updatedDuration != null) updatedFilm.setDuration(updatedDuration);
        if (name != null) updatedFilm.setName(name);
        if (description != null) updatedFilm.setDescription(description);
        log.info("Film updated {}", updatedFilm);
        return updatedFilm;
    }

    private int getNextId() {
        return nextId += 1;
    }
}
