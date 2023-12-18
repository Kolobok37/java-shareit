package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private User owner;

    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private Long requestId;
}
