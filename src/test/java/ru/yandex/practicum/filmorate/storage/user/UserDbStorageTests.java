package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.row.UserRowMapper;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage userStorage;
    private static final String INSERT_USER =
            "INSERT INTO users (id, name, login, email, birthday) VALUES " +
                    "           (1, 'Test', 'login', 'test@mail.com', '1992-03-03')";
    private static final String INSERT_USERS =
            "INSERT INTO users (id, name, login, email, birthday) VALUES " +
                    "          (1, 'Test', 'login', 'test@mail.com', '1992-03-03')," +
                    "          (2, 'Test2', 'login2', 'test2@mail.com', '1992-03-03')";

    @Test
    void shouldAddUser() {
        User user = new User();
        user.setName("Test");
        user.setLogin("login");
        user.setEmail("test@mail.com");

        User created = userStorage.addUser(user);

        assertThat(created)
                .isNotNull()
                .extracting(
                        User::getId,
                        User::getName,
                        User::getLogin,
                        User::getEmail)
                .containsExactly(
                        1L,
                        created.getName(),
                        created.getLogin(),
                        created.getEmail());
    }

    @Test
    @Sql(statements = {INSERT_USER})
    void shouldFindUserById() {
        Optional<User> result = userStorage.getUserById(1);

        assertThat(result).hasValueSatisfying(u -> {
            assertThat(u.getName()).isEqualTo("Test");
            assertThat(u.getLogin()).isEqualTo("login");
        });
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        Collection<User> users = userStorage.getUsers();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    @Sql(statements = {INSERT_USERS})
    void shouldReturnAllUsers() {
        Collection<User> users = userStorage.getUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@mail.com", "test2@mail.com");
    }

    @Test
    @Sql(statements = {INSERT_USER})
    void shouldUpdateUserName() {
        User updateUser = new User();
        updateUser.setId(1);
        updateUser.setName("Test2");

        User updated = userStorage.updateUser(updateUser);

        assertThat(updated)
                .extracting(
                        User::getId,
                        User::getName,
                        User::getLogin,
                        User::getEmail)
                .containsExactly(
                        updated.getId(),
                        "Test2",
                        updated.getLogin(),
                        updated.getEmail()
                );
    }

    @Test
    @Sql(statements = {INSERT_USER})
    void shouldUpdateAllUserFields() {

        User updateUser = new User();
        updateUser.setId(1);
        updateUser.setName("Test2");
        updateUser.setLogin("login2");
        updateUser.setEmail("test2@mail.com");

        User updated = userStorage.updateUser(updateUser);

        assertThat(updated)
                .extracting(
                        User::getId,
                        User::getName,
                        User::getLogin,
                        User::getEmail)
                .containsExactly(
                        updateUser.getId(),
                        "Test2",
                        "login2",
                        "test2@mail.com"
                );
    }

    @Test
    void shouldFailWhenUserNotExists() {
        User user = new User();
        user.setName("Test");
        user.setId(1L);

        assertThatThrownBy(() -> userStorage.updateUser(user))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");
    }
}
