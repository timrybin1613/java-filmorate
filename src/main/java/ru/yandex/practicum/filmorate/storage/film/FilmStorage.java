package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film addFilm(Film film);

    Optional<Film> getFilmById(long id);

    Film updateFilm(Film film);

    boolean existsFilmById(long id);

}
