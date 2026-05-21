package ru.yandex.practicum.filmorate.mapper.row;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getDouble(("duration")));
        film.setMpa((rs.getLong("mpa_id")));
        film.setGenres(new HashSet<>());
        film.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);

        return film;
    }
}
