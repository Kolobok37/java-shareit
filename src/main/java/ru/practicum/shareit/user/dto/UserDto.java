package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private int id;
    @NotNull(message = "Name is not be null")
    private String name;
    @Email(message = "Email is not valid")
    @NotEmpty(message = "Email is not be empty")
    @NotNull(message = "Email is not be null")
    private String email;

    @Override
    public String toString() {
        return id + " " + name + " " + email;
    }
}
