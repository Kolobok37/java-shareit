package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User user;
    private List<ItemDto> items;
}
