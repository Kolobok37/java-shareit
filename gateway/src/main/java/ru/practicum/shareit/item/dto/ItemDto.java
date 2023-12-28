package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private UserDto owner;
    @NotBlank(message = "Name cannot be empty.")
    private String name;
    @NotBlank(message = "Description cannot be empty.")
    private String description;
    @NotNull(message = "Available cannot be empty.")
    private Boolean available;
    private List<CommentDto> comments;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private Long requestId;
}
