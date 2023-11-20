package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailDataException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    @Qualifier("userDBStorage")
    private UserStorage userStorage;

    public UserDto createUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationDataException("Email can't be empty.");
        }
        if (user.getName() == null) {
            throw new ValidationDataException("Email can't be empty.");
        }
        User newUser = userStorage.createUser(user);
        try {
            checkExistsEmail(newUser);
        } catch (DuplicateEmailDataException e) {
            userStorage.deleteUser(newUser.getId());
            throw new DuplicateEmailDataException("This email already exists");
        }
        return MapperUser.mapToUserDto(userStorage.createUser(user));
    }

    public UserDto getUser(Long userId) {
        return MapperUser.mapToUserDto(userStorage.getUser(userId));
    }

    public UserDto updateUser(User user) {
        checkExistsEmail(user);
        if (user.getEmail() == null) {
            user.setEmail(userStorage.getUser(user.getId()).getEmail());
        }
        if (user.getName() == null) {
            user.setName(userStorage.getUser(user.getId()).getName());
        }
        return MapperUser.mapToUserDto(userStorage.updateUser(user));
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public List<UserDto> getAllUser() {
        return userStorage.getAllUser().stream().map(MapperUser::mapToUserDto).collect(Collectors.toList());
    }

    private void checkExistsEmail(User user) throws DuplicateEmailDataException {
        if (getAllUser().stream().anyMatch(user1 -> user1.getEmail()
                .equals(user.getEmail()) && user1.getId() != user.getId())) {
            throw new DuplicateEmailDataException("This email already exists");
        }
    }
}
