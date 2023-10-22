package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NotNull
@AllArgsConstructor
public class Reviews {
    int userId;
    String reviews;
    LocalDate date;
}
