package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.row.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String SELECT_ALL_MPA = "SELECT id, name FROM mpa";
    private static final String SELECT_MPA_BY_ID = "SELECT id, name FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc, MpaRowMapper mapper) {
        super(jdbc, namedJdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        return findMany(SELECT_ALL_MPA);
    }

    @Override
    public Optional<Mpa> findById(long id) {
        return findOne(SELECT_MPA_BY_ID, id);
    }
}
