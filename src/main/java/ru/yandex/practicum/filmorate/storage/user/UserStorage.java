package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    Optional<User> getUserById(Integer userId);

    User addUser(User user);

    User updateUser(User user);

    boolean existsUserById(Integer id);
}
