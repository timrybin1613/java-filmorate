package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public FilmResponseDto getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public Collection<FilmResponseDto> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public FilmResponseDto addFilm(@Valid @RequestBody FilmCreateDto filmCreateDto) {
        log.info("Adding film {}", filmCreateDto);
        FilmResponseDto addFilm = filmService.addFilm(filmCreateDto);
        log.info("Film added {}", addFilm);
        return addFilm;
    }

    @PutMapping
    public FilmResponseDto updateFilm(@Valid @RequestBody FilmUpdateDto filmUpdateDto) {
        log.info("Updating film {}", filmUpdateDto);
        FilmResponseDto updatedFilm = filmService.updateFilm(filmUpdateDto);
        log.info("Film updated {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Adding like, filmId - {}, userId - {}", id, userId);

        filmService.addLikeToFilm(id, userId);

        log.info("Like added, filmId - {}, userId - {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Deleting like, filmId - {}, userId - {}", id, userId);

        filmService.removeLikeFromFilm(id, userId);

        log.info("Like deleted, filmId - {}, userId - {}", id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmResponseDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Getting popular films from database, count={}", count);
        return filmService.getPopularFilms(count);
    }

}
