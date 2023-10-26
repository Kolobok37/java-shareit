package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User getUser(int userId);

    User updateUser(User user);

    void deleteUser(int userId);

    List<User> getAllUser();
}
