package ru.yandex.practicum.filmorate.storage.user.friendship;

import java.util.Set;

public interface FriendshipStorage {

    void addFriendship(Integer userId, Integer friendId);

    void removeFriendship(Integer userId, Integer friendId);

    Set<Integer> getFriends(Integer userId);

    Set<Integer> getCommonFriendsId(Integer userId, Integer friendId);

}
