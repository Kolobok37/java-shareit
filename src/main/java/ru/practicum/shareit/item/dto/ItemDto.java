package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Reviews;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    private UserDto owner;
    @NotNull(message = "Name is not be null")
    @NotEmpty(message = "Name is not be empty")
    private String name;
    @NotNull(message = "Description is not be null")
    @NotEmpty(message = "Description is not be empty")
    private String description;
    @NotNull(message = "Available is not be null")
    private Boolean available;
    private List<Booking> reservation;
    private List<Reviews> reviews;
    private ItemRequest request;
}
