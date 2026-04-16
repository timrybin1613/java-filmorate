package ru.yandex.practicum.filmorate.storage.film.like;

public interface FilmLikeStorage {

    void addFilmLike(Integer filmId, Integer userId);

    void removeFilmLike(Integer filmId, Integer userId);

    int getCountLikesByFilmId(Integer filmId);
}
