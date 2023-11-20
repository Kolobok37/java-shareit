package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @ManyToOne
    @JoinColumn(name = "item")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker")
    private User booker;

    @Column(name = "date_comment", nullable = false)
    LocalDateTime created;
    @NotEmpty(message = "Text cannot be empty")
    @Column(name = "text", nullable = false)
    private String text;

    public Comment() {
    }
}

