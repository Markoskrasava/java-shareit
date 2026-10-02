package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long newId = 1L;

    public User addUser(User user) {
        user.setId(newId);
        users.put(newId, user);
        newId++;
        return user;
    }

    public User patchUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(Long userId) {
        if (userId == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
