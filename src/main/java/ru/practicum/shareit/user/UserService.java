package ru.practicum.shareit.user;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailDataException;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Service
public class UserService {

    @Autowired
    @Qualifier("userDBStorage")
    private UserStorage userStorage;

    public UserDto createUser(User user) {
        try {
            return MapperUser.mapToUserDto(userStorage.createUser(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailDataException("This email already exists");
        }
    }

    public UserDto getUser(Long userId) {
        return MapperUser.mapToUserDto(userStorage.getUser(userId));
    }

    public UserDto updateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(userStorage.getUser(user.getId()).getEmail());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(userStorage.getUser(user.getId()).getName());
        }
        try {
            return MapperUser.mapToUserDto(userStorage.createUser(user));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailDataException("This email already exists");
        }
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public List<UserDto> getAllUser() {
        return userStorage.getAllUser().stream().map(MapperUser::mapToUserDto).collect(Collectors.toList());
    }
}
