package ru.yandex.practicum.filmorate.model.user;

import lombok.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;

    public User(String login, String email, String name, LocalDate birthday) {
        this.login = login;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }
}