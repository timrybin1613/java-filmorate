package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.row.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTests {
    private final JdbcTemplate jdbc;
    private final GenreDbStorage genreDbStorage;

    @Test
    @Sql("/data.sql")
    void shouldReturnAllGenres() {
        List<Genre> result = genreDbStorage.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Sql("/data.sql")
    void shouldReturnCorrectCount() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM genre",
                Integer.class
        );

        List<Genre> result = genreDbStorage.findAll();

        assertEquals(count, result.size());
    }

    @Test
    @Sql("/data.sql")
    void shouldFindById() {
        Genre genre = genreDbStorage.findAll().get(0);

        Optional<Genre> result = genreDbStorage.findById(genre.getId());

        assertTrue(result.isPresent());
        assertEquals(genre.getId(), result.get().getId());
    }

    @Test
    @Sql("/data.sql")
    void shouldReturnEmptyIfNotFound() {
        Optional<Genre> result = genreDbStorage.findById(9999);

        assertTrue(result.isEmpty());
    }

    @Test
    @Sql("/data.sql")
    void shouldReturnGenresByIds() {
        List<Genre> all = genreDbStorage.findAll();

        List<Long> ids = List.of(
                all.get(0).getId(),
                all.get(1).getId()
        );

        List<Genre> result = genreDbStorage.findByIds(ids);

        assertEquals(2, result.size());
    }

    @Test
    @Sql("/data.sql")
    void shouldIgnoreNonExistingIds() {
        List<Genre> result = genreDbStorage.findByIds(List.of(9999L));

        assertTrue(result.isEmpty());
    }
}
