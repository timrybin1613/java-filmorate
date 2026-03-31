package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int nextId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Adding user {}", userCreateDto);
        String login = userCreateDto.getLogin();
        String name = userCreateDto.getName();

        int id = getNextId();
        User user = new User();

        user.setId(id);
        user.setLogin(login);

        if (name == null || name.isBlank()) {
            name = login;
        }

        user.setName(name);
        user.setEmail(userCreateDto.getEmail());
        user.setBirthday(userCreateDto.getBirthday());

        users.put(user.getId(), user);
        log.info("Added user {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Updating user {}", userUpdateDto);
        int id = userUpdateDto.getId();

        User user = Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        String login = userUpdateDto.getLogin();
        String name = userUpdateDto.getName();
        String email = userUpdateDto.getEmail();
        LocalDate birthday = userUpdateDto.getBirthday();

        if (login != null && !login.isEmpty()) user.setLogin(login);
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        if (birthday != null) user.setBirthday(birthday);
        log.info("Updated user {}", user);
        return user;
    }

    private int getNextId() {
        return nextId += 1;
    }
}
