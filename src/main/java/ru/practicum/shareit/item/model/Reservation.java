package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NotNull
public class Reservation {
    private LocalDate from;
    private LocalDate to;
    private int userId;
}
