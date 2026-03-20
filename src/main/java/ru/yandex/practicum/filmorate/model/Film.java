package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id", "name"})
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Double duration;
}
