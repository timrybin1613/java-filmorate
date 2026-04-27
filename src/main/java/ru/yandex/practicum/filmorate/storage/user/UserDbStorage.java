package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.row.UserRowMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Qualifier
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String SELECT_ALL_USERS = "SELECT * FROM users;";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?;";
    private static final String INSERT_USER = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?);";

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbc, namedParameterJdbcTemplate, mapper);
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(SELECT_ALL_USERS);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return findOne(SELECT_USER_BY_ID, userId);
    }

    @Override
    public User addUser(User user) {
        long id = insert(INSERT_USER, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        return findOne(SELECT_USER_BY_ID, id).orElseThrow(() -> new RuntimeException("User not added"));
    }

    @Override
    public User updateUser(User user) {
        if (!existsUserById(user.getId())) {
            throw new UserNotFoundException("User not found");
        }

        StringBuilder sql = new StringBuilder("UPDATE users SET ");
        List<Object> paramsList = new ArrayList<>();

        if (user.getName() != null) {
            sql.append("name=?, ");
            paramsList.add(user.getName());
        }
        if (user.getEmail() != null) {
            sql.append("email=?, ");
            paramsList.add(user.getEmail());
        }
        if (user.getLogin() != null) {
            sql.append("login=?, ");
            paramsList.add(user.getLogin());
        }
        if (user.getBirthday() != null) {
            sql.append("birthday=?, ");
            paramsList.add(user.getBirthday());
        }

        if (paramsList.isEmpty()) {
            throw new InternalServerException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        paramsList.add(user.getId());

        update(sql.toString(), paramsList.toArray());

        return findOne(SELECT_USER_BY_ID, user.getId()).orElseThrow(() -> new RuntimeException("Error updating user"));
    }

    @Override
    public boolean existsUserById(long id) {
        return findOne(SELECT_USER_BY_ID, id).isPresent();
    }
}
