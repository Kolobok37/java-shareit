package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    @Qualifier("userStorageInMemory")
    private UserStorage userStorage;

    public UserDto createUser(User user) {
        validation(user);
        checkExistsEmail(user);
        userStorage.createUser(user);
        return MapperUser.mapToUserDto(user);
    }

    public UserDto getUser(int userId) {
        return MapperUser.mapToUserDto(userStorage.getUser(userId));
    }

    public UserDto updateUser(User user) {
        checkExistsEmail(user);
        return MapperUser.mapToUserDto(userStorage.updateUser(user));
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public List<UserDto> getAllUser() {
        return userStorage.getAllUser().stream().map(user -> MapperUser.mapToUserDto(user)).collect(Collectors.toList());
    }

    private void validation(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email is not valid");
        }
        if (user.getEmail().indexOf("@") == -1) {
            throw new ValidationException("Email is not valid");
        }
    }

    private void checkExistsEmail(User user) {
        if (getAllUser().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()) && user1.getId() != user.getId())
                .findFirst().isPresent()) {
            throw new DuplicateEmailException("This email already exists");
        }
    }
}
