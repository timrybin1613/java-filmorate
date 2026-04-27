package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;


public interface FriendshipStorage {

    void addFriendship(Friendship friendship);

    void updateFriendshipStatus(FriendshipStatus friendshipStatus, Integer user1Id, Integer user2Id);

    Optional<Friendship> findFriendship(Integer userId, Integer friendId);

    void removeFriendship(Friendship friendship);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriendsId(Integer userId, Integer friendId);

}
