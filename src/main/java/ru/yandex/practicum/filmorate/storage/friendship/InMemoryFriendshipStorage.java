package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final List<Friendship> friendships = new ArrayList<>();
    private final UserStorage userStorage;

    public InMemoryFriendshipStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriendship(Friendship friendship) {
        friendships.add(friendship);
    }

    @Override
    public void updateFriendshipStatus(FriendshipStatus friendshipStatus, Integer user1Id, Integer user2Id) {
        friendships.stream()
                .filter(f -> f.getUser1Id().equals(user1Id) && f.getUser2Id().equals(user2Id))
                .findFirst().ifPresent(friendship -> friendship.setStatus(friendshipStatus));
    }

    @Override
    public Optional<Friendship> findFriendship(Integer user1Id, Integer user2Id) {
        return friendships.stream()
                .filter(f -> f.getUser1Id().equals(user1Id) && f.getUser2Id().equals(user2Id)).findFirst();
    }

    @Override
    public void removeFriendship(Friendship friendship) {
        friendships.remove(friendship);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        return friendships.stream()
                .filter(f -> (f.getStatus().equals(FriendshipStatus.CONFIRMED) &&
                        (f.getUser1Id().equals(userId) || f.getUser2Id().equals(userId)))
                        || (f.getStatus().equals(FriendshipStatus.PENDING) && f.getRequesterId().equals(userId)))
                .map(f -> {
                    if (f.getUser1Id().equals(userId)) {
                        return f.getUser2Id();
                    } else {
                        return f.getUser1Id();
                    }
                })
                .map(id -> userStorage.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<User> getCommonFriendsId(Integer userId, Integer friendId) {
        return null;
    }
}
