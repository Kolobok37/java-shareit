package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class MapperUser {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapToUser(UserDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }

}
