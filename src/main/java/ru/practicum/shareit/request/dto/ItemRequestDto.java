package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {
    private int id;
    private String description;
    private User requestor;
    private LocalDate created;
}
