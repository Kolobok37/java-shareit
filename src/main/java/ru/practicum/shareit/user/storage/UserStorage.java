package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUser(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);

    List<User> getAllUser();
}
