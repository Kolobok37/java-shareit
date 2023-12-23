package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperComment;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Test
    @SneakyThrows
    void createItem_whenDataIsCorrect() {
        Item item = new Item(null, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);

        when(itemService.createItem(1L, item)).thenReturn(MapperItem.mapToItemDto(item));

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(MapperItem.mapToItemDto(item)), result);
    }

    @Test
    @SneakyThrows
    void createItem_whenDataIsNotCorrect_thenReturnValidationException() {
        Item item = new Item(null, new User(1L, "alex", "alex1@gmail.com"), null,
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);

        when(itemService.createItem(1L, item)).thenReturn(MapperItem.mapToItemDto(item));

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());


        verify(itemService, never()).createItem(1L, item);
    }

    @Test
    @SneakyThrows
    void createComment_whenDataIsCorrect_thenReturnOkAndCommentDto() {
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        User user = new User(1L,"alex","alex@gmail.com");
        Comment comment = new Comment();
        comment.setText("comment1");
        comment.setItem(item);
        comment.setBooker(user);
        comment.setId(1L);
        long userId = 1L;
        long itemId = 1L;

        when(itemService.createComment(userId, itemId, comment)).thenReturn(MapperComment.mapToCommentDto(comment));

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(MapperComment.mapToCommentDto(comment)),result);
    }

    @Test
    @SneakyThrows
    void createComment_whenDataIsNotCorrect_thenReturnBedRequest() {
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        User user = new User(1L,"alex","alex@gmail.com");
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setBooker(user);
        long userId = 1L;
        long itemId = 1L;

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verify(itemService,never()).createComment(userId,itemId,comment);
    }

    @Test
    @SneakyThrows
    void getItem() {
        long itemId = 1L;
        long userId = 1L;

        mockMvc.perform(get("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).getItemDto(itemId, userId);
    }

    @Test
    @SneakyThrows
    void getAllUsersItem() {
        long userId = 1L;
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/items").param("from", from).param("size", size).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).getAllUsersItem(Integer.valueOf(from), Optional.of(Integer.valueOf(size)), userId);
    }

    @Test
    @SneakyThrows
    void searchItem() {
        String text = "ussr";
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/items/search").param("from", from).param("text", text).param("size", size))
                .andExpect(status().isOk());

        verify(itemService).searchItem(Integer.valueOf(from), Optional.of(Integer.valueOf(size)), text);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDto itemDto = new ItemDto(1l, null, "alex1", null, null, null,
                null, null, null);
        when(itemService.updateItem(1L, 1L, MapperItem.mapToItem(itemDto))).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }
}