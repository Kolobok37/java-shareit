package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemBookingDto {
    private Long id;
    private User owner;

    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> reviews;
    private Long lastBookingId;
    private Long nextBookingId;
}
