package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Collection<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserResponseDto addUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Adding user {}", userCreateDto);
        UserResponseDto createdUser = userService.addUser(userCreateDto);
        log.info("Added user {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public UserResponseDto updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Updating user {}", userUpdateDto);
        UserResponseDto updatedUser = userService.updateUser(userUpdateDto);
        log.info("Updated user {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Adding friend userId - {}, friendId - {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Added friend userId - {}, friendId - {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Deleting friend userId - {}, friendId - {}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Deleted friend userId - {}, friendId - {}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Integer id) {
        log.info("Getting friends for userId - {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Getting friends for userId - {}, otherId - {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
