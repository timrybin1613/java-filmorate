package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.row.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@Transactional
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {

    private static final String INSERT_FILM_SQL =
            "INSERT INTO film (id, name, description, mpa_id, release_date, duration) VALUES " +
                    "         (1, 'film', 'test', 1, '2000-01-01', 1)";
    private static final String INSERT_TWO_FILMS_SQL =
            "INSERT INTO film (id, name, description, mpa_id, release_date, duration) VALUES " +
                    "         (1, 'film', 'test', 1, '2000-01-01', 1), " +
                    "         (2, 'film2', 'test2', 2, '2000-01-01', 2)";
    private static final String INSERT_FILM_GENRES_FOR_FILM_1 =
            "INSERT INTO film_genre (film_id, genre_id) VALUES " +
                    "               (1, 1), (1, 2)";
    private final FilmDbStorage filmDbStorage;

    @Test
    void shouldAddFilmWithoutGenres() {
        Film film = new Film();
        film.setName("test");
        film.setMpa(1L);
        film.setDuration(11D);
        film.setReleaseDate(LocalDate.now());

        assertThat(filmDbStorage.addFilm(film))
                .extracting(
                        Film::getName,
                        Film::getGenres)
                .containsExactly(
                        "test",
                        new HashSet<>()
                );
    }

    @Test
    void shouldAddFilmWithGenres() {
        Film film = new Film();
        film.setName("test");
        film.setMpa(1L);
        film.setDuration(11D);
        film.setReleaseDate(LocalDate.now());

        Set<Long> genres = new HashSet<>();
        genres.add(1L);
        genres.add(2L);
        film.setGenres(genres);

        assertThat(filmDbStorage.addFilm(film))
                .extracting(
                        Film::getId,
                        Film::getName,
                        Film::getGenres)
                .containsExactly(
                        1L,
                        "test",
                        genres
                );
    }

    @Test
    @Sql("/data.sql")
    @Sql(statements = {INSERT_FILM_SQL, INSERT_FILM_GENRES_FOR_FILM_1})
    void shouldReturnFilmWithGenres() {
        assertThat(filmDbStorage.getFilmById(1)).hasValueSatisfying(film -> {
            AssertionsForInterfaceTypes.assertThat(film.getGenres()).containsExactlyInAnyOrder(1L, 2L);
        });
    }

    @Test
    @Sql("/data.sql")
    @Sql(statements = {INSERT_FILM_SQL, INSERT_FILM_GENRES_FOR_FILM_1})
    void shouldReturnEmptyOptionalWhenFilmNotFound() {
        assertThat(filmDbStorage.getFilmById(2)).isEmpty();
    }

    @Test
    @Sql("/data.sql")
    @Sql(statements = {INSERT_TWO_FILMS_SQL, INSERT_FILM_GENRES_FOR_FILM_1})
    void shouldReturnAllFilmsWithGenres() {
        Collection<Film> films = filmDbStorage.getFilms();
        AssertionsForInterfaceTypes.assertThat(films).size().isEqualTo(2);
        films.forEach(film -> {
            if (film.getId().equals(1L)) {
                assertThat(film.getGenres().size()).isEqualTo(2);
            } else {
                assertThat(film.getGenres().isEmpty());
            }
        });
    }

    @Test
    @Sql("/data.sql")
    @Sql(statements = {INSERT_FILM_SQL})
    void shouldUpdateOnlyProvidedFields() {

        assertThat(filmDbStorage.getFilmById(1L)).hasValueSatisfying(film -> {
            assertThat(film.getName().equals("film")).isTrue();
        });

        Film film = new Film();
        film.setId(1L);
        film.setName("film2");

        assertThat(filmDbStorage.getFilmById(1L)).hasValueSatisfying(film1 -> {
            assertThat(film.getName().equals("film2")).isTrue();
        });
    }

    @Test
    @Sql("/data.sql")
    @Sql(statements = {INSERT_FILM_SQL})
    void shouldThrowWhenNoFieldsToUpdate() {
        Film film = new Film();
        film.setId(1L);

        assertThatThrownBy(() -> filmDbStorage.updateFilm(film))
                .isInstanceOf(InternalServerException.class);
    }

}
