package ru.yandex.practicum.filmorate.mapper.row;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();

        friendship.setUser1Id(rs.getInt("user1_id"));
        friendship.setUser2Id(rs.getInt("user2_id"));
        FriendshipStatus status = FriendshipStatus.from(rs.getString("status"));
        friendship.setStatus(status);
        friendship.setRequesterId(rs.getInt("requester_id"));

        return friendship;
    }
}
