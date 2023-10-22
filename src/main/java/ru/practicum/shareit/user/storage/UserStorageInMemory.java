package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserStorageInMemory implements UserStorage {

    private int id;
    private HashMap<Integer, User> userStorage;

    public UserStorageInMemory(HashMap<Integer, User> userStorage) {
        this.id = 1;
        this.userStorage = userStorage;
    }

    @Override

    public User createUser(User user) {
        userStorage.put(id, user);
        user.setId(id);
        id++;
        return user;
    }

    @Override
    public User getUser(int userId) {
        return userStorage.get(userId);
    }

    @Override
    public User updateUser(User user) {
        if (user.getEmail() == null) {
            user.setEmail(userStorage.get(user.getId()).getEmail());
        }
        if (user.getName() == null) {
            user.setName(userStorage.get(user.getId()).getName());
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        userStorage.remove(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userStorage.values()
                .stream()
                .collect(Collectors.toList());
    }
}
