package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private int id;
    private long item;
    private String authorName;
    LocalDateTime created;
    private String text;

}
