package ru.practicum.shareit.usersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.MapperUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @SneakyThrows
    @Test
    void getUser() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUser() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUser();
    }

    @SneakyThrows
    @Test
    void createUser_whenDataIsCorrect_thenReturnOkAndUserDto() {
        User createdUser = new User(null, "alex", "alex@gmail.com");

        when(userService.createUser(createdUser)).thenReturn(MapperUser.mapToUserDto(createdUser));

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(MapperUser.mapToUserDto(createdUser)), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenDataIsEmpty_thenReturnBedRequest() {
        User createdUser = new User(null, "", "alex@gmail.com");
        User createdUser1 = new User(null, "alex", null);


        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createdUser1)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(createdUser);
        verify(userService, never()).createUser(createdUser1);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        User updateUser = new User(1L, "alex1", "alex1@gmail.com");

        when(userService.updateUser(updateUser)).thenReturn(MapperUser.mapToUserDto(updateUser));


        String result = mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(MapperUser.mapToUserDto(updateUser)), result);
    }

}
