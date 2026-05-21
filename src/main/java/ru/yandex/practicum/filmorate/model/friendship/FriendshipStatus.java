package ru.yandex.practicum.filmorate.model.friendship;

public enum FriendshipStatus {
    PENDING,
    CONFIRMED;

    public static FriendshipStatus from(String value) {
        return FriendshipStatus.valueOf(value.toUpperCase());
    }
}