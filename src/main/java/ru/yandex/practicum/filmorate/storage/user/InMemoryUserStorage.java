package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int nextId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsUserById(long userId) {
        return users.containsKey(userId);
    }

    private long getNextId() {
        return nextId += 1;
    }
}
