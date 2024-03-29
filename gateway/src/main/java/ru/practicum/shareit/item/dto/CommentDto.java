package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long item;
    private String authorName;
    LocalDateTime created;
    @NotBlank(message = "Text cannot be empty.")
    private String text;
}
