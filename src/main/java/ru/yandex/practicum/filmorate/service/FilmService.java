package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreFilmCreateDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.dto.FilmDtoMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmLikeStorage filmLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmDtoMapper filmDtoMapper;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmService(FilmLikeStorage filmLikeStorage,
                       FilmStorage filmStorage,
                       UserStorage userStorage,
                       FilmDtoMapper filmDtoMapper,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmLikeStorage = filmLikeStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmDtoMapper = filmDtoMapper;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public FilmResponseDto getFilmById(long filmId) {
        log.debug("getFilmById {}", filmId);

        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> {
            log.warn("Film with id {} not found", filmId);
            return new FilmNotFoundException("Film with id " + filmId + " not found");
        });

        List<Genre> genres = loadGenresForFilm(film);
        Mpa mpa = loadMpaForFilm(film).orElse(null);

        return filmDtoMapper.toFilmResponse(film, mpa, genres);
    }

    public Collection<FilmResponseDto> getFilms() {
        log.debug("get all films");
        Collection<FilmResponseDto> filmsResponseDto = filmStorage.getFilms().stream()
                .map(film -> {
                    List<Genre> genres = loadGenresForFilm(film);
                    Mpa mpa = loadMpaForFilm(film).orElse(null);
                    return filmDtoMapper.toFilmResponse(film, mpa, genres);
                })
                .toList();
        log.debug("Returned - {} films", filmsResponseDto.size());
        return filmsResponseDto;
    }

    public FilmResponseDto addFilm(FilmCreateDto dto) {
        log.debug("add film {}", dto);
        genreExists(dto.getGenres());
        mpaExist(dto.getMpa().getId());
        Film createdFilm = filmDtoMapper.toFilm(dto);

        Film newFilm = filmStorage.addFilm(createdFilm);
        List<Genre> genres = loadGenresForFilm(newFilm);
        Mpa mpa = loadMpaForFilm(newFilm).orElse(null);

        return filmDtoMapper.toFilmResponse(newFilm, mpa, genres);
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

        if (dto.getMpa() != null) {
            mpaExist(dto.getMpa().getId());
            filmToUpdate.setMpa(dto.getMpa().getId());
        }

        if (dto.getGenres() != null) {
            genreExists(dto.getGenres());
            Set<Long> newGenres = new HashSet<>();
            dto.getGenres().forEach(genre -> {
                newGenres.add(genre.getId());
            });
            filmToUpdate.setGenres(newGenres);
        }

        Film updated = filmStorage.updateFilm(filmToUpdate);

        List<Genre> genres = loadGenresForFilm(updated);
        Mpa mpa = loadMpaForFilm(updated).orElse(null);

        log.debug("updated film {}", updated);

        return filmDtoMapper.toFilmResponse(updated, mpa, genres);
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        log.debug("add like to film {}, userId {}", filmId, userId);
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmLikeStorage.addFilmLike(filmId, userId);
    }

    public void removeLikeFromFilm(Long filmId, Long userId) {
        log.debug("remove like to film {}, userId {}", filmId, userId);
        validateFilmExists(filmId);
        validateUserExists(userId);

        filmLikeStorage.removeFilmLike(filmId, userId);
    }

    public List<FilmResponseDto> getPopularFilms(long count) {
        log.debug("get popular films, counts - {}", count);
        List<FilmResponseDto> filmsResponseDto = filmStorage.getFilms().stream()
                .sorted(Comparator.comparingLong(film -> filmLikeStorage.getCountLikesByFilmId(film.getId())))
                .limit(count)
                .map(film -> {
                    List<Genre> genres = loadGenresForFilm(film);
                    Mpa mpa = loadMpaForFilm(film).orElse(null);
                    return filmDtoMapper.toFilmResponse(film, mpa, genres);
                })
                .toList()
                .reversed();

        log.debug("Returned - {} films", filmsResponseDto.size());

        return filmsResponseDto;
    }

    private void validateUserExists(Long userId) {
        if (!userStorage.existsUserById(userId)) {
            log.error("User with id {} not found", userId);
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    private void validateFilmExists(Long filmId) {
        if (!filmStorage.existsFilmById(filmId)) {
            log.warn("Film with id {} not found", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " not found");
        }
    }

    private List<Genre> loadGenresForFilm(Film film) {
        log.debug("load genres");

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            log.debug("genres is empty");
            return Collections.emptyList();
        }

        List<Genre> genres = genreStorage.findByIds(film.getGenres());
        log.debug("Returned - {} genres", genres.size());
        return genres;
    }

    private Optional<Mpa> loadMpaForFilm(Film film) {
        if (film.getMpa() == null) {
            log.debug("mpa is null");
            return Optional.empty();
        }
        return mpaStorage.findById(film.getMpa());
    }

    private void mpaExist(long id) {
        if (mpaStorage.findById(id).isEmpty()) {
            throw new MpaNotFoundException("Mpa with id " + id + " not found");
        }
    }

    private void genreExists(Set<GenreFilmCreateDto> genre) {
        if (genre == null || genre.isEmpty()) {
            log.debug("genre is null or empty");
            return;
        }

        Set<Long> requestGenreIds = genre.stream()
                .map(GenreFilmCreateDto::getId)
                .collect(Collectors.toSet());

        Set<Long> existingIds = genreStorage.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!existingIds.containsAll(requestGenreIds)) {
            throw new GenreNotFoundException("Genre not found");
        }
    }
}
