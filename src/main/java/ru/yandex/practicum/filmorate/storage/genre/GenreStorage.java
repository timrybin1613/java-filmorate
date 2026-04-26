package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(long id);

    List<Genre> findByIds(Collection<Long> ids);
}
