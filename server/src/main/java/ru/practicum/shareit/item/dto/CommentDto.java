package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private long item;
    private String authorName;
    ZonedDateTime created;
    private String text;

}
