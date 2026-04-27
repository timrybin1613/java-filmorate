package ru.yandex.practicum.filmorate.model.film;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id", "name"})
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Long> genres;
    private Long mpa;
    private Double duration;
}
