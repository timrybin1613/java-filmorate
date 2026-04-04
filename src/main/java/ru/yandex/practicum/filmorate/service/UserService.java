package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.user.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.friendship.FriendshipStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final FriendshipStorage friendshipStorage;
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserService(FriendshipStorage friendshipStorage,
                       UserStorage userStorage,
                       UserMapper userMapper) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserResponseDto getUserById(Integer userId) {
        log.debug("getUserById({})", userId);

        return userMapper.toUserResponseDto(
                userStorage.getUserById(userId)
                        .orElseThrow(() -> {
                            log.warn("getUserById({}) not found", userId);
                            return new UserNotFoundException("User with id: " + userId + " not found");
                        }));
    }

    public Collection<UserResponseDto> getUsers() {
        log.debug("get all users");

        Collection<UserResponseDto> usersResponseDto = userStorage.getUsers().stream()
                .map(userMapper::toUserResponseDto)
                .toList();
        log.debug("Returned - {} users", usersResponseDto.size());
        return usersResponseDto;
    }

    public UserResponseDto addUser(UserCreateDto dto) {
        log.debug("add user {}", dto);
        String login = dto.getLogin();
        dto.setName((dto.getName() == null || dto.getName().isBlank() ? login : dto.getName()));
        return userMapper.toUserResponseDto(userStorage.addUser(userMapper.toUser(dto)));
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

        return userMapper.toUserResponseDto(updated);
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.debug("add friend, userId {}, friendId {}", userId, friendId);
        if (userId.equals(friendId)) {
            log.warn("User cannot add themselves as a friend. userId {} friendId {}", userId, friendId);
            throw new IllegalArgumentException("User cannot add themselves as a friend");
        }

        validateUserExists(userId);
        validateUserExists(friendId);

        friendshipStorage.addFriendship(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        log.debug("remove friend, userId {}, friendId {}", userId, friendId);
        validateUserExists(userId);
        validateUserExists(friendId);

        friendshipStorage.removeFriendship(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        log.debug("get friends, userId {}", userId);
        validateUserExists(userId);

        Set<Integer> friendIds = friendshipStorage.getFriends(userId);
        log.debug("Returned - {} friends", friendIds.size());
        return friendIds.stream().map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        log.debug("get common friends, userId {}, friendId {}", userId, friendId);
        validateUserExists(userId);
        validateUserExists(friendId);

        Set<Integer> commonFriendIds = friendshipStorage.getCommonFriendsId(userId, friendId);
        log.debug("Returned - {} common friends", commonFriendIds.size());
        return commonFriendIds.stream().map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsUserById(userId)) {
            log.warn("User with id {} not found", userId);
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }
}
