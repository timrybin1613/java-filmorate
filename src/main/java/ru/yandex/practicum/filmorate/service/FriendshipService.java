package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.dto.UserDtoMapper;
import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;
    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    public FriendshipService(FriendshipStorage friendshipStorage, UserStorage userStorage) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
        this.userDtoMapper = new UserDtoMapper();
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.debug("Adding friendship for users with id - {}, {}", userId, friendId);
        Integer user1Id = Integer.min(userId, friendId);
        Integer user2Id = Integer.max(userId, friendId);
        validateUserExists(userId);
        validateUserExists(friendId);
        Optional<Friendship> exist = friendshipStorage.findFriendship(user1Id, user2Id);

        if (exist.isPresent()) {
            Friendship friendship = exist.get();
            FriendshipStatus friendshipStatus = friendship.getStatus();
            log.debug("Friendship exists");
            log.debug("Friendship status - {}", friendshipStatus);

            if (friendship.getStatus().equals(FriendshipStatus.PENDING)) {
                if (friendship.getRequesterId().equals(friendId)) {
                    friendship.setStatus(FriendshipStatus.CONFIRMED);
                    friendshipStorage.updateFriendshipStatus(friendshipStatus,
                            friendship.getUser1Id(),
                            friendship.getUser2Id());
                }
            }
        } else {
            log.debug("add new friendship for users with id - {}, {}", userId, friendId);
            Friendship friendship = new Friendship();
            friendship.setUser1Id(userId);
            friendship.setUser2Id(friendId);
            friendship.setRequesterId(userId);
            friendship.setStatus(FriendshipStatus.PENDING);
            friendshipStorage.addFriendship(friendship);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        log.debug("Remove friendship for users with id - {}, {}", userId, friendId);
        validateUserExists(userId);
        validateUserExists(friendId);
        Optional<Friendship> exist = friendshipStorage.findFriendship(userId, friendId);
        exist.ifPresent(friendshipStorage::removeFriendship);
    }

    public Collection<UserResponseDto> getFriends(Integer userId) {
        log.debug("Get friends for users with id - {}", userId);
        validateUserExists(userId);
        List<User> friends = friendshipStorage.getFriends(userId);
        if (!friends.isEmpty()) {
            log.debug("Friends count - {}", friends.size());
            return friends.stream().map(userDtoMapper::toUserResponseDto)
                    .collect(Collectors.toList());
        }
        log.debug("No friends for users with id - {}", userId);
        return new ArrayList<>();
    }

    public Collection<UserResponseDto> getCommonFriends(Integer userId, Integer friendId) {
        log.debug("Get common friends for users with id - {}, {}", userId, friendId);

        if (Objects.equals(userId, friendId)) {
            log.warn("Call method getCommonFriends for self");
            return new ArrayList<>();
        }

        validateUserExists(userId);
        validateUserExists(friendId);

        List<User> commonFriends = friendshipStorage.getCommonFriendsId(userId, friendId);
        if (!commonFriends.isEmpty()) {
            log.debug("Common friends count - {}", commonFriends.size());
            return commonFriends.stream().map(userDtoMapper::toUserResponseDto)
                    .collect(Collectors.toList());
        }
        log.debug("No common friends for users with id - {}, {}", userId, friendId);
        return new ArrayList<>();
    }

    private void validateUserExists(Integer userId) {
        if (!userStorage.existsUserById(userId)) {
            log.warn("User with id {} not found", userId);
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }
}

