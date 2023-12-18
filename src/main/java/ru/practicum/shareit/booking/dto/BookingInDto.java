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
    private Long id;
    @NotNull(message = "Not be null.")
    private LocalDateTime start;
    @NotNull(message = "Not be null.")
    private LocalDateTime end;
    private Long itemId;

    private UserDto bookerId;
    private Status status;
    }
