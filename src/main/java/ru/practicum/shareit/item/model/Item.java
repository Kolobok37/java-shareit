package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private int id;
    private User owner;
    private String name;
    private String description;
    private Boolean available;
    private List<Booking> reservation;
    private List<Reviews> reviews;
    private ItemRequest request;

    public Item(User owner, String name, String description) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        reservation = new ArrayList<>();
        reviews = new ArrayList<>();
    }
}
