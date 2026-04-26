package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmLikeDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikeDbStorageTests {

    private final JdbcTemplate jdbc;
    private final FilmLikeDbStorage filmLikeDbStorage;
    private static final String INSERT_FILM_SQL =
            "INSERT INTO film (id, name, description, mpa_id, release_date, duration) VALUES" +
                    "         (1, 'film', 'test', 1, '2000-01-01', 1)";
    private static final String INSERT_FILM_LIKE = "INSERT INTO film_like (film_id, user_id) VALUES (1, 1)";
    private static final String INSERT_USER_SQL =
            "INSERT INTO users (id, name, login, email, birthday) VALUES " +
                    "          (1, 'Test', 'login', 'test@mail.com', '1992-03-03')";
    private static final String INSERT_USERS_SQL =
            "INSERT INTO users (id, name, login, email, birthday) " +
                    "VALUES (1, 'Test', 'login', 'test@mail.com', '1992-03-03')," +
                    "       (2, 'Test2', 'login2', 'test2@mail.com', '1992-03-03')," +
                    "       (3, 'Test3', 'login3', 'test3@mail.com', '1992-03-03')";

    private final long userId = 1;
    private final long filmId = 1;

    @Test
    @Sql(statements = {INSERT_FILM_SQL, INSERT_USER_SQL, INSERT_FILM_LIKE})
    void shouldAddLike() {

        filmLikeDbStorage.addFilmLike(filmId, userId);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM film_like WHERE film_id = ? AND user_id = ?",
                Integer.class,
                filmId, userId
        );
        assertEquals(1, count);
    }

    @Test
    @Sql(statements = {INSERT_FILM_SQL, INSERT_USER_SQL})
    void shouldNotDuplicateLike() {
        filmLikeDbStorage.addFilmLike(filmId, userId);
        filmLikeDbStorage.addFilmLike(filmId, userId);

        int count = filmLikeDbStorage.getCountLikesByFilmId(filmId);

        assertEquals(1, count);
    }

    @Test
    @Sql(statements = {INSERT_FILM_SQL, INSERT_USER_SQL})
    void shouldRemoveLike() {
        filmLikeDbStorage.addFilmLike(filmId, userId);

        filmLikeDbStorage.removeFilmLike(filmId, userId);

        int count = filmLikeDbStorage.getCountLikesByFilmId(filmId);

        assertEquals(0, count);
    }

    @Test
    @Sql(statements = {INSERT_FILM_SQL, INSERT_USER_SQL})
    void shouldHandleRemovingNonExistingLike() {
        filmLikeDbStorage.removeFilmLike(filmId, userId);

        int count = filmLikeDbStorage.getCountLikesByFilmId(filmId);

        assertEquals(0, count);
    }

    @Test
    @Sql(statements = {INSERT_FILM_SQL, INSERT_USERS_SQL})
    void shouldReturnCorrectLikeCount() {
        filmLikeDbStorage.addFilmLike(filmId, 1);
        filmLikeDbStorage.addFilmLike(filmId, 2);
        filmLikeDbStorage.addFilmLike(filmId, 3);

        int count = filmLikeDbStorage.getCountLikesByFilmId(filmId);

        assertEquals(3, count);
    }
}
