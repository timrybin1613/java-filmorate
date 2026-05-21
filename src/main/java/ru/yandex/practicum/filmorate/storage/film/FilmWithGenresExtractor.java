package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class FilmWithGenresExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        LinkedHashMap<Long, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null);
                film.setGenres(new HashSet<>());
                film.setDuration(rs.getDouble("duration"));
                film.setMpa(rs.getLong("mpa_id"));
                filmMap.put(film.getId(), film);
            }
            Long genreId = rs.getObject("genre_id", Long.class);
            if (genreId != null) {
                filmMap.get(filmId).getGenres().add(genreId);
            }
        }
        return new ArrayList<>(filmMap.values());
    }
}
