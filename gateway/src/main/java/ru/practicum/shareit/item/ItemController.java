package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    @Autowired
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto item) {
        log.info("Creating item, userId={}", userId);
        return itemClient.createItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Positive @PathVariable Long itemId, @RequestBody @Valid CommentDto text) {
        log.info("Creating item, userId={}", userId);
        return itemClient.createComment(userId, itemId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @PathVariable Long itemId,
                                          @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item, itemId = {}, userId = {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item user, userId = {}", userId);
        return itemClient.getAllUserItem(userId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items text = {}", text);
        return itemClient.searchItem(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Update item ItemId = {}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}

