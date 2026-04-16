package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.annotation.NoSpacesLogin;

import java.time.LocalDate;

@Data
public class UserCreateDto {

    @NotBlank
    @NoSpacesLogin
    private String login;

    @NotBlank
    @Email
    private String email;

    private String name;

    @NotNull
    @Past
    private LocalDate birthday;
}
