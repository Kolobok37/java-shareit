package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAllUser() {
        return userService.getAllUser();

    }

    @PostMapping(consumes = {"application/json"})
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(value = "/{userId}", consumes = {"application/json"})
    public UserDto updateUser(@Valid @RequestBody User user, @PathVariable Long userId) {
        user.setId(userId);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
