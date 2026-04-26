package ru.yandex.practicum.filmorate.storage.like;

public interface FilmLikeStorage {

    void addFilmLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);

    int getCountLikesByFilmId(long filmId);
}
