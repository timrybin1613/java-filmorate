package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@Primary
public class FilmLikeDbStorage implements FilmLikeStorage {

    private final JdbcTemplate jdbc;
    private static final String INSERT_LIKE_FOR_FILM = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_FOR_FILM = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String SELECT_COUNT_LIKES_FOR_FILM = "SELECT COUNT(*) FROM film_like WHERE film_id = ?";

    public FilmLikeDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void addFilmLike(long filmId, long userId) {
        try {
            jdbc.update(INSERT_LIKE_FOR_FILM, filmId, userId);
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public void removeFilmLike(long filmId, long userId) {
        int rows = jdbc.update(DELETE_LIKE_FOR_FILM, filmId, userId);

        if (rows == 0) {
            log.debug("Like not found for film {} and user {}", filmId, userId);
        }
    }

    @Override
    public int getCountLikesByFilmId(long filmId) {
        Integer result = jdbc.queryForObject(SELECT_COUNT_LIKES_FOR_FILM, Integer.class, filmId);
        return Optional.ofNullable(result).orElse(0);
    }
}
