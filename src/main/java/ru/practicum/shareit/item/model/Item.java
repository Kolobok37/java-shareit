package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private User owner;
    @Column(nullable = false)
    @NotNull(message = "Name is not be null")
    @NotEmpty(message = "Name is not be empty")
    private String name;
    @Column(nullable = false)
    @NotNull(message = "Description is not be null")
    @NotEmpty(message = "Description is not be empty")
    private String description;
    @Column(nullable = false)
    @NotNull(message = "Available is not be null")
    private Boolean available;

    @ElementCollection
    @PrimaryKeyJoinColumn(name = "id")
    private List<Comment> comments;

    @OneToOne
    private Booking lastBooking;

    @OneToOne
    private Booking nextBooking;

    public Item() {
    }

}
