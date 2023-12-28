package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingItemDto {
    private Long id;
    @NotNull(message = "Not be null.")
    private LocalDateTime start;
    @NotNull(message = "Not be null.")
    private LocalDateTime end;
    private Long itemId;

    private UserDto bookerId;
    private BookingState status;
}
