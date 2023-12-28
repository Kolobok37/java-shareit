package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private LocalDateTime created;
    private UserDto user;
    private List<ItemDto> items;
}