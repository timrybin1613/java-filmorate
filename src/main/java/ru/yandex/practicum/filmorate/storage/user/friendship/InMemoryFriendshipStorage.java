package ru.yandex.practicum.filmorate.storage.user.friendship;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Integer, Set<Integer>> friendship = new HashMap<>();

    @Override
    public void addFriendship(Integer userId, Integer friendId) {
        friendship.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendship.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriendship(Integer userId, Integer friendId) {
        Set<Integer> friends = friendship.get(userId);
        Set<Integer> friendFriends = friendship.get(friendId);

        if (friendFriends != null) {
            friendFriends.remove(userId);
        }

        if (friends != null) {
            friends.remove(friendId);
        }
    }

    @Override
    public Set<Integer> getFriends(Integer userId) {
        if (friendship.containsKey(userId)) {
            return friendship.get(userId);
        }
        return new HashSet<>();
    }

    @Override
    public Set<Integer> getCommonFriendsId(Integer userId, Integer friendId) {

        Set<Integer> userFriends = friendship.get(userId);
        Set<Integer> friendFriends = friendship.get(friendId);

        if (userFriends == null || friendFriends == null) {
            return Collections.emptySet();
        }

        return userFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
