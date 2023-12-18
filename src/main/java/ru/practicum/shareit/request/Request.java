package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long id;
    @Column(name = "description")
    @NotBlank(message = "Description is not be empty.")
    private String description;
    @Column(name = "date_created",nullable = false)
    private LocalDateTime created;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}
