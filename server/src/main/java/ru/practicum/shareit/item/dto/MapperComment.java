package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapperComment {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getItem().getId(), comment.getBooker().getName(), comment.getCreated(),
                comment.getText());
    }

    public static List<CommentDto> mapToCommentDto(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments.stream().map(comment -> MapperComment.mapToCommentDto(comment)).collect(Collectors.toList());
    }

}
