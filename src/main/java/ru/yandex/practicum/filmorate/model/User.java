package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id", "email"})
public class User {
    private int id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;
}