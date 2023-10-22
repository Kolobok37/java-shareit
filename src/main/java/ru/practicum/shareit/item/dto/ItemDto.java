package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Reviews;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    private UserDto user;
    private String name;
    private String description;
    private Boolean available;
    private ArrayList<Booking> reservation;
    private ArrayList<Reviews> reviews;
    private ItemRequest request;
}
