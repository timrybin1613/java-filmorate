package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDto {

    private int id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;

}
