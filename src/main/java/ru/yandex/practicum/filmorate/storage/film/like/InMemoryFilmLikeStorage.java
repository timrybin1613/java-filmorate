package ru.yandex.practicum.filmorate.storage.film.like;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final HashMap<Integer, Set<Integer>> filmsLikes = new HashMap<>();

    @Override
    public void addFilmLike(Integer filmId, Integer userId) {
        filmsLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFilmLike(Integer filmId, Integer userId) {
        if (filmsLikes.containsKey(filmId)) {
            Set<Integer> userIds = filmsLikes.get(filmId);
            if (userIds != null) {
                userIds.remove(userId);
            }
        }
    }

    @Override
    public int getCountLikesByFilmId(Integer filmId) {
        return filmsLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}
