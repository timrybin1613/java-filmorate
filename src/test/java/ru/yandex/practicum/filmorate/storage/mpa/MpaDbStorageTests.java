package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.row.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTests {
    private final JdbcTemplate jdbc;
    private final MpaDbStorage mpaDbStorage;

    @Test
    @Sql("/data.sql")
    void shouldReturnAllMpa() {
        List<Mpa> result = mpaDbStorage.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Sql("/data.sql")
    void shouldReturnCorrectCount() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM mpa",
                Integer.class
        );

        List<Mpa> result = mpaDbStorage.findAll();

        assertEquals(count, result.size());
    }

    @Test
    @Sql("/data.sql")
    void shouldFindMpaById() {
        Mpa mpa = mpaDbStorage.findAll().get(0);

        Optional<Mpa> result = mpaDbStorage.findById(mpa.getId());

        assertTrue(result.isPresent());
        assertEquals(mpa.getId(), result.get().getId());
    }

    @Test
    @Sql("/data.sql")
    void shouldReturnEmptyIfNotFound() {
        Optional<Mpa> result = mpaDbStorage.findById(9999);

        assertTrue(result.isEmpty());
    }
}
