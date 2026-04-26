package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final HashMap<Long, Set<Long>> filmsLikes = new HashMap<>();

    @Override
    public void addFilmLike(long filmId, long userId) {
        filmsLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFilmLike(long filmId, long userId) {
        if (filmsLikes.containsKey(filmId)) {
            Set<Long> userIds = filmsLikes.get(filmId);
            if (userIds != null) {
                userIds.remove(userId);
            }
        }
    }

    @Override
    public int getCountLikesByFilmId(long filmId) {
        return filmsLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}
