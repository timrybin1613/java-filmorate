package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.like.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmLikeStorage filmLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;

    public FilmService(FilmLikeStorage filmLikeStorage,
                       FilmStorage filmStorage,
                       UserStorage userStorage,
                       FilmMapper filmMapper) {
        this.filmLikeStorage = filmLikeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmMapper = filmMapper;
    }

    public FilmResponseDto getFilmById(int filmId) {
        log.debug("getFilmById {}", filmId);
        return filmMapper.toFilmResponse(
                filmStorage.getFilmById(filmId)
                        .orElseThrow(() -> {
                            log.warn("Film with id {} not found", filmId);
                            return new FilmNotFoundException("Film with id " + filmId + " not found");
                        }));
    }

    public Collection<FilmResponseDto> getFilms() {
        log.debug("get all films");
        Collection<FilmResponseDto> filmsResponseDto = filmStorage.getFilms().stream()
                .map(filmMapper::toFilmResponse)
                .toList();
        log.debug("Returned - {} films", filmsResponseDto.size());
        return filmsResponseDto;
    }

    public FilmResponseDto addFilm(FilmCreateDto dto) {
        log.debug("add film {}", dto);
        return filmMapper.toFilmResponse(filmStorage.addFilm(filmMapper.toFilm(dto)));
    }

    public FilmResponseDto updateFilm(FilmUpdateDto dto) {
        log.debug("update film {}", dto);
        Film filmToUpdate = filmStorage.getFilmById(dto.getId())
                .orElseThrow(() -> {
                    log.warn("Film with id {} not found", dto.getId());
                    return new FilmNotFoundException("Film with id " + dto.getId() + " not found");
                });

        if (dto.getDuration() != null) filmToUpdate.setDuration(dto.getDuration());
        if (dto.getName() != null) filmToUpdate.setName(dto.getName());
        if (dto.getDescription() != null) filmToUpdate.setDescription(dto.getDescription());
        if (dto.getReleaseDate() != null) filmToUpdate.setReleaseDate(dto.getReleaseDate());

        Film updated = filmStorage.updateFilm(filmToUpdate);
        log.debug("updated film {}", updated);

        return filmMapper.toFilmResponse(updated);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        log.debug("add like to film {}, userId {}", filmId, userId);
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmLikeStorage.addFilmLike(filmId, userId);
    }

    public void removeLikeFromFilm(Integer filmId, Integer userId) {
        log.debug("remove like to film {}, userId {}", filmId, userId);
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmLikeStorage.removeFilmLike(filmId, userId);
    }

    public List<FilmResponseDto> getPopularFilms(int count) {
        log.debug("get popular films, counts - {}", count);
        List<FilmResponseDto> filmsResponseDto = filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(film -> filmLikeStorage.getCountLikesByFilmId(film.getId())))
                .limit(count)
                .map(filmMapper::toFilmResponse)
                .toList()
                .reversed();

        log.debug("Returned - {} films", filmsResponseDto.size());

        return filmsResponseDto;
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsUserById(userId)) {
            log.error("User with id {} not found", userId);
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    private void validateFilmExists(Integer filmId) {
        if (!filmStorage.existsFilmById(filmId)) {
            log.warn("Film with id {} not found", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " not found");
        }
    }
}
