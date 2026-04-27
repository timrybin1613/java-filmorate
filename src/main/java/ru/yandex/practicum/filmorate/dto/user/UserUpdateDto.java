package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotation.NoSpacesLogin;

import java.time.LocalDate;

@Data
public class UserUpdateDto {

    @NotNull
    @Positive
    private Long id;

    @NoSpacesLogin
    private String login;

    @Email
    private String email;

    private String name;

    @Past
    private LocalDate birthday;
}
