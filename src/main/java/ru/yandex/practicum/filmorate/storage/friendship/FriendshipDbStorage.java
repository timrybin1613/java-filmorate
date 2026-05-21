package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.row.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.mapper.row.UserRowMapper;
import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class FriendshipDbStorage extends BaseRepository<Friendship> implements FriendshipStorage {

    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final String INSERT_FRIENDSHIP_REQUEST = "INSERT INTO friendship (user1_id, user2_id, requester_id, status) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_FRIENDSHIP_STATUS = "UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?;";
    private static final String SELECT_FRIENDSHIP_BY_USERS_IDS = "SELECT user1_id, user2_id, requester_id, status FROM friendship WHERE user1_id = ? AND user2_id = ?;";
    private static final String DELETED_FRIENDSHIP = "DELETE FROM friendship WHERE user1_id = ? AND user2_id = ?;";
    private static final String SELECT_FRIENDS_FOR_USER = """
            SELECT u.id,
                   u.name,
                   u.login,
                   u.email,
                   u.birthday
            FROM friendship f
            JOIN users u ON u.id =
                CASE
                    WHEN f.user1_id = :uid1 THEN f.user2_id
                    ELSE f.user1_id
                END
            WHERE (f.user1_id = :uid1 OR f.user2_id = :uid1)
            AND (f.status = 'CONFIRMED' OR (f.status = 'PENDING' AND f.requester_id = :uid1));
            """;
    private static final String SELECT_COMMON_FRIEND_FOR_USERS = """
            SELECT u.id,
                   u.name,
                   u.login,
                   u.email,
                   u.birthday
            FROM friendship f1
            JOIN users u ON u.id =
                CASE
                    WHEN f1.user1_id = :uid1 THEN f1.user2_id
                    ELSE f1.user1_id
                END
            WHERE (f1.user1_id = :uid1 OR f1.user2_id = :uid1)
              AND (
                    f1.status = 'CONFIRMED'
                    OR (f1.status = 'PENDING' AND f1.requester_id = :uid1)
                  )
              AND u.id IN (
                    SELECT CASE
                               WHEN f2.user1_id = :uid2 THEN f2.user2_id
                               ELSE f2.user1_id
                           END
                    FROM friendship f2
                    WHERE (f2.user1_id = :uid2 OR f2.user2_id = :uid2)
                      AND (
                            f2.status = 'CONFIRMED'
                            OR (f2.status = 'PENDING' AND f2.requester_id = :uid2)
                          )
              );
            """;

    public FriendshipDbStorage(JdbcTemplate jdbc, NamedParameterJdbcTemplate namedJdbc, FriendshipRowMapper mapper, UserStorage userStorage) {
        super(jdbc, namedJdbc, mapper);
    }

    @Override
    public Optional<Friendship> findFriendship(Integer user1Id, Integer user2Id) {
        return findOne(SELECT_FRIENDSHIP_BY_USERS_IDS, user1Id, user2Id);
    }

    @Override
    public void updateFriendshipStatus(FriendshipStatus friendshipStatus, Integer user1Id, Integer user2Id) {
        update(UPDATE_FRIENDSHIP_STATUS, friendshipStatus.toString(), user1Id, user2Id);
    }

    @Override
    public void addFriendship(Friendship friendship) {
        update(INSERT_FRIENDSHIP_REQUEST,
                friendship.getUser1Id(),
                friendship.getUser2Id(),
                friendship.getRequesterId(),
                friendship.getStatus().name());
    }

    @Override
    public void removeFriendship(Friendship friendship) {
        delete(DELETED_FRIENDSHIP, friendship.getUser1Id(), friendship.getUser2Id());
    }

    @Override
    public List<User> getFriends(Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("uid1", userId);
        return findManyWithNamedParams(SELECT_FRIENDS_FOR_USER, params, USER_ROW_MAPPER);
    }

    @Override
    public List<User> getCommonFriendsId(Integer userId, Integer friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("uid1", userId);
        params.addValue("uid2", friendId);
        return findManyWithNamedParams(SELECT_COMMON_FRIEND_FOR_USERS, params, USER_ROW_MAPPER);
    }
}
