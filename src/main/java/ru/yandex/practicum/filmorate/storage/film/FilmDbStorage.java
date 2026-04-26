package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.row.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.film.Film;

import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.*;

@Primary
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String SELECT_FILM_BY_ID = "SELECT * FROM film WHERE id=?";
    private static final String SELECT_FILM_BY_ID_WITH_GENRES = """
            SELECT
                f.id,
                f.name,
                f.description,
                f.mpa_id,
                f.release_date,
                f.duration,
                fg.genre_id
            FROM film f
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            WHERE f.id = ?
            """;
    private static final String SELECT_ALL_FILM_WITH_GENRES = """
            SELECT
                f.id,
                f.name,
                f.description,
                f.mpa_id,
                f.release_date,
                f.duration,
                fg.genre_id
            FROM film f
            LEFT JOIN film_genre fg ON f.id = fg.film_id
            ORDER BY f.id;
            """;
    private static final String DELETE_GENRES_BY_FILM_ID = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM =
            "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbc, namedParameterJdbcTemplate, mapper);
    }

    @Override
    public Collection<Film> getFilms() {
        LinkedHashMap<Long, Film> filmMap = new LinkedHashMap<>();
        jdbc.query(SELECT_ALL_FILM_WITH_GENRES, rs -> {

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
        });
        return filmMap.values();
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa());

        if (film.getGenres() != null) insertGenresForFilm(id, film.getGenres());
        return getFilmById(id).orElseThrow(() -> new InternalServerException("Film not added"));
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        LinkedHashMap<Long, Film> filmMap = new LinkedHashMap<>();
        jdbc.query(SELECT_FILM_BY_ID_WITH_GENRES, rs -> {
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

        }, id);

        return filmMap.values().stream().findFirst();
    }

    @Override
    public Film updateFilm(Film film) {
        StringBuilder sql = new StringBuilder("UPDATE film SET ");
        List<Object> paramsList = new ArrayList<>();

        if (film.getName() != null) {
            sql.append("name = ?, ");
            paramsList.add(film.getName());
        }

        if (film.getDescription() != null) {
            sql.append("description = ?, ");
            paramsList.add(film.getDescription());
        }

        if (film.getDuration() != null) {
            sql.append("duration = ?, ");
            paramsList.add(film.getDuration());
        }

        if (film.getReleaseDate() != null) {
            sql.append("release_date = ?, ");
            paramsList.add(film.getReleaseDate());
        }

        if (film.getMpa() != null) {
            sql.append("mpa_id = ?, ");
            paramsList.add(film.getMpa());
        }

        if (film.getGenres() != null) {
            deleteGenresForFilm(film.getId());
            insertGenresForFilm(film.getId(), film.getGenres());
        }

        if (paramsList.isEmpty()) {
            throw new InternalServerException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        paramsList.add(film.getId());
        update(sql.toString(), paramsList.toArray());

        return getFilmById(film.getId()).orElseThrow(() -> new InternalServerException("Error updating film"));
    }

    @Override
    public boolean existsFilmById(long id) {
        return findOne(SELECT_FILM_BY_ID, id).isPresent();
    }

    private void insertGenresForFilm(long filmId, Set<Long> genreIds) {
        for (Long id : genreIds) {
            insertWithoutKey(INSERT_FILM_GENRE, filmId, id);
        }
    }

    private void deleteGenresForFilm(long filmId) {
        delete(DELETE_GENRES_BY_FILM_ID, filmId);
    }
}
