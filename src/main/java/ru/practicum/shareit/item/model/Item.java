package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private int id;
    private User user;
    private String name;
    private String description;
    private Boolean available;
    private ArrayList<Booking> reservation;
    private ArrayList<Reviews> reviews;
    private ItemRequest request;

    public Item(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
        reservation = new ArrayList<>();
        reviews = new ArrayList<>();
    }
}
