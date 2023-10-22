package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private int id;
    private String name;
    private String email;

    @Override
    public String toString() {
        return id + " " + name + " " + email;
    }
}
