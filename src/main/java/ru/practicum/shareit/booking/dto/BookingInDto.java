package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInDto {
    private long id;
    @NotNull(message = "Not be null.")
    private LocalDateTime start;
    @NotNull(message = "Not be null.")
    private LocalDateTime end;
    private long itemId;

    private UserDto bookerId;
    private Status status;
    }
