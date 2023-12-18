package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "Name can't be empty.")
    private String name;
    @Column(nullable = false)
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email can't be empty.")
    @Email(message = "Email is not valid.")
    private String email;

    public User() {
    }

    @Override
    public String toString() {
        return id + " " + name + " " + email;
    }
}
