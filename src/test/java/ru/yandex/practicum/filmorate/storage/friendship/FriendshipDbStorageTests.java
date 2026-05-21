package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.row.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.mapper.row.UserRowMapper;
import ru.yandex.practicum.filmorate.model.friendship.Friendship;
import ru.yandex.practicum.filmorate.model.friendship.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class,
        UserRowMapper.class,
        FriendshipDbStorage.class,
        FriendshipRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipDbStorageTests {
    private final JdbcTemplate jdbc;
    private final FriendshipDbStorage friendshipDbStorage;

    private static final String ADD_TWO_USERS_SQL =
            "INSERT INTO users (id, name, login, email, birthday) VALUES " +
                    "          (1, 'Test', 'login', 'test@mail.com', '1992-03-03')," +
                    "          (2, 'Test2', 'login2', 'test2@mail.com', '1992-03-03')";
    private static final String ADD_FOUR_USERS_SQL =
            "INSERT INTO users (id, name, login, email, birthday) VALUES " +
                    "          (1, 'Test', 'login', 'test@mail.com', '1992-03-03')," +
                    "          (2, 'Test2', 'login2', 'test2@mail.com', '1992-03-03')," +
                    "          (3, 'Test3', 'login3', 'test3@mail.com', '1992-03-03')," +
                    "          (4, 'Test4', 'login4', 'test4@mail.com', '1992-03-03')";
    private static final String ADD_FRIENDSHIP_FOR_UID1_AND_UID2_PENDING_SQL =
            "INSERT INTO friendship (user1_id, user2_id, requester_id, status) VALUES " +
                    "               (1, 2, 1, 'PENDING')";
    private static final String ADD_FRIENDSHIP_FOR_UID1_AND_UID2_CONFIRMED_SQL =
            "INSERT INTO friendship (user1_id, user2_id, requester_id, status) VALUES " +
                    "               (1, 2, 1, 'CONFIRMED')";
    private static final String COUNT_FRIENDSHIP_SQL = "SELECT COUNT(*) FROM friendship";
    private static final String ADD_FRIENDSHIP_FOR_FOUR_USERS_WITH_COMMON_FRIENDS =
            "INSERT INTO friendship (user1_id, user2_id, requester_id, status) VALUES" +
                    "               (1, 2, 1, 'PENDING')," +
                    "               (1, 3, 1, 'CONFIRMED')," +
                    "               (2, 3, 2, 'CONFIRMED')," +
                    "               (1, 4, 1, 'CONFIRMED')," +
                    "               (2, 4, 2, 'CONFIRMED')";
    private static final String ADD_FRIENDSHIP_FOR_USERS_WITHOUT_COMMON_FRIENDS =
            "INSERT INTO friendship (user1_id, user2_id, requester_id, status) VALUES" +
                    "               (1, 2, 1, 'PENDING')," +
                    "               (2, 4, 2, 'CONFIRMED')," +
                    "               (3, 4, 3, 'CONFIRMED')";

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    void shouldAddFriendship() {

        int user1Id = 1;
        int user2Id = 2;

        String countSql = "SELECT COUNT(*) FROM friendship WHERE user1_id = ? AND user2_id = ?";

        Friendship friendship = new Friendship();
        friendship.setUser1Id(user1Id);
        friendship.setUser2Id(user2Id);
        friendship.setRequesterId(user1Id);
        friendship.setStatus(FriendshipStatus.PENDING);

        Integer before = jdbc.queryForObject(countSql, Integer.class, user1Id, user2Id);
        assertThat(before).isEqualTo(0);

        friendshipDbStorage.addFriendship(friendship);

        Integer after = jdbc.queryForObject(countSql, Integer.class, user1Id, user2Id);
        assertThat(after).isEqualTo(1);
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_PENDING_SQL)
    void shouldUpdateStatusFromPendingToConfirmed() {
        friendshipDbStorage.updateFriendshipStatus(FriendshipStatus.CONFIRMED, 1, 2);
        assertThat(friendshipDbStorage.findFriendship(1, 2))
                .hasValueSatisfying(friendship -> {
                    assertThat(friendship.getStatus().equals(FriendshipStatus.CONFIRMED)).isTrue();
                });
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_PENDING_SQL)
    void shouldNotUpdateIfFriendshipNotExists() {
        assertThatThrownBy(() -> friendshipDbStorage
                .updateFriendshipStatus(FriendshipStatus.CONFIRMED, 2, 3))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_PENDING_SQL)
    void shouldReturnFriendshipWhenExists() {
        assertThat(friendshipDbStorage.findFriendship(1, 2))
                .hasValueSatisfying(friendship -> {
                    assertThat(friendship.getUser1Id()).isEqualTo(1);
                    assertThat(friendship.getUser2Id()).isEqualTo(2);
                    assertThat(friendship.getStatus().equals(FriendshipStatus.PENDING)).isTrue();
                });
    }

    @Test
    void shouldReturnEmptyWhenFriendshipNotExists() {
        assertThat(friendshipDbStorage.findFriendship(1, 2)).isEmpty();
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_CONFIRMED_SQL)
    void shouldReturnEmptyWhenUserIdsNotNormalized() {
        assertThat(friendshipDbStorage.findFriendship(1, 2))
                .hasValueSatisfying(
                        friendship -> {
                            assertThat(friendship.getUser1Id()).isEqualTo(1);
                            assertThat(friendship.getUser2Id()).isEqualTo(2);
                            assertThat(friendship.getStatus().equals(FriendshipStatus.CONFIRMED)).isTrue();
                        }
                );
        assertThat(friendshipDbStorage.findFriendship(2, 1)).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenUserIdsAreNull() {
        assertThat(friendshipDbStorage.findFriendship(1, null)).isEmpty();
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_CONFIRMED_SQL)
    void shouldDeleteExistingFriendship() {
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(1);
        friendshipDbStorage.removeFriendship(new Friendship(1, 2, FriendshipStatus.CONFIRMED, 1));
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(0);
    }

    @Test
    void shouldDoNothingWhenFriendshipNotExists() {
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(0);
        friendshipDbStorage.removeFriendship(new Friendship(1, 2, FriendshipStatus.CONFIRMED, 1));
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_CONFIRMED_SQL)
    void shouldBeIdempotentWhenDeletingTwice() {
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(1);
        friendshipDbStorage.removeFriendship(new Friendship(1, 2, FriendshipStatus.CONFIRMED, 1));
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(0);
        friendshipDbStorage.removeFriendship(new Friendship(1, 2, FriendshipStatus.CONFIRMED, 1));
        assertThat(jdbc.queryForObject(COUNT_FRIENDSHIP_SQL, Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_CONFIRMED_SQL)
    void shouldReturnFriendsForUser() {
        AssertionsForInterfaceTypes.assertThat(friendshipDbStorage.getFriends(1))
                .hasSize(1).first().extracting(User::getId).isEqualTo(2L);
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    void shouldReturnEmptyListWhenUserHasNoFriends() {
        assertThat(friendshipDbStorage.getFriends(1).size()).isEqualTo(0);
    }

    @Test
    @Sql(statements = ADD_TWO_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_UID1_AND_UID2_PENDING_SQL)
    void shouldIncludePendingWhenUserIsRequester() {
        AssertionsForInterfaceTypes.assertThat(friendshipDbStorage.getFriends(1))
                .hasSize(1).first().extracting(User::getId).isEqualTo(2L);

        AssertionsForInterfaceTypes.assertThat(friendshipDbStorage.getFriends(2))
                .hasSize(0);
    }

    @Test
    @Sql(statements = ADD_FOUR_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_FOUR_USERS_WITH_COMMON_FRIENDS)
    void shouldReturnCommonFriendsForTwoUsers() {
        List<User> users = friendshipDbStorage.getCommonFriendsId(1, 2);
        AssertionsForInterfaceTypes.assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(3L, 4L);
    }

    @Test
    @Sql(statements = ADD_FOUR_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_USERS_WITHOUT_COMMON_FRIENDS)
    void shouldNotReturnUsersThatAreNotCommon() {
        List<User> users = friendshipDbStorage.getCommonFriendsId(1, 2);
        AssertionsForInterfaceTypes.assertThat(users).isEmpty();
    }

    @Test
    @Sql(statements = ADD_FOUR_USERS_SQL)
    @Sql(statements = ADD_FRIENDSHIP_FOR_FOUR_USERS_WITH_COMMON_FRIENDS)
    void shouldReturnSameResultWhenUsersSwapped() {
        List<User> users = friendshipDbStorage.getCommonFriendsId(1, 2);
        AssertionsForInterfaceTypes.assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(3L, 4L);
        List<User> users2 = friendshipDbStorage.getCommonFriendsId(2, 1);
        AssertionsForInterfaceTypes.assertThat(users2)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(3L, 4L);
    }
}
