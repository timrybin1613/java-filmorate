package ru.yandex.practicum.filmorate.model.friendship;

import lombok.Data;

@Data
public class Friendship {
    private Integer user1Id;
    private Integer user2Id;
    private FriendshipStatus status;
    private Integer requesterId;

    public Friendship() {
    }

    public Friendship(Integer user1Id, Integer user2Id, FriendshipStatus status, Integer requesterId) {
        this.user1Id = Math.min(user1Id, user2Id);
        this.user2Id = Math.max(user2Id, user1Id);
        this.status = status;
        this.requesterId = requesterId;
    }
}
