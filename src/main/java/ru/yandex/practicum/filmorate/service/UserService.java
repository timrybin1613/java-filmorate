package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.dto.UserDtoMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    public UserService(FriendshipStorage friendshipStorage,
                       UserStorage userStorage,
                       UserDtoMapper userDtoMapper) {
        this.userStorage = userStorage;
        this.userDtoMapper = userDtoMapper;
    }

    public UserResponseDto getUserById(Integer userId) {
        log.debug("getUserById({})", userId);

        return userDtoMapper.toUserResponseDto(
                userStorage.getUserById(userId)
                        .orElseThrow(() -> {
                            log.warn("getUserById({}) not found", userId);
                            return new UserNotFoundException("User with id: " + userId + " not found");
                        }));
    }

    public Collection<UserResponseDto> getUsers() {
        log.debug("get all users");

        Collection<UserResponseDto> usersResponseDto = userStorage.getUsers().stream()
                .map(userDtoMapper::toUserResponseDto)
                .toList();
        log.debug("Returned - {} users", usersResponseDto.size());
        return usersResponseDto;
    }

    public UserResponseDto addUser(UserCreateDto dto) {
        log.debug("add user {}", dto);
        String login = dto.getLogin();
        dto.setName((dto.getName() == null || dto.getName().isBlank() ? login : dto.getName()));
        return userDtoMapper.toUserResponseDto(userStorage.addUser(userDtoMapper.toUser(dto)));
    }

    public UserResponseDto updateUser(UserUpdateDto dto) {
        User userToUpdate = userStorage.getUserById(dto.getId())
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", dto.getId());
                    return new UserNotFoundException("User with id: " + dto.getId() + " not found");
                });

        if (dto.getLogin() != null && !dto.getLogin().isEmpty()) userToUpdate.setLogin(dto.getLogin());
        if (dto.getName() != null) userToUpdate.setName(dto.getName());
        if (dto.getEmail() != null) userToUpdate.setEmail(dto.getEmail());
        if (dto.getBirthday() != null) userToUpdate.setBirthday(dto.getBirthday());

        User updated = userStorage.updateUser(userToUpdate);
        log.debug("updated user {}", updated);

        return userDtoMapper.toUserResponseDto(updated);
    }
}
