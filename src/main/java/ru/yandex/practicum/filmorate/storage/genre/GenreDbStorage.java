package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.row.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.*;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String SELECT_ALL_GENRES = "SELECT id, name FROM genre ORDER BY id";
    private static final String SELECT_GENRE_BY_ID = "SELECT id, name FROM genre WHERE id = ?";
    private static final String SELECT_GENRES_BY_IDS = "SELECT id, name FROM genre WHERE id IN (:ids) ORDER BY id";

    public GenreDbStorage(JdbcTemplate jdbc,
                          GenreRowMapper mapper,
                          NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbc, namedParameterJdbcTemplate, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(SELECT_ALL_GENRES);
    }

    @Override
    public Optional<Genre> findById(long id) {
        return findOne(SELECT_GENRE_BY_ID, id);
    }

    @Override
    public List<Genre> findByIds(Collection<Long> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        params.addValue("ids", ids);
        return findManyWithNamedParams(SELECT_GENRES_BY_IDS, params, mapper);
    }
}
