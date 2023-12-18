package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId, @Valid @RequestBody Comment text) {
        return itemService.createComment(userId, itemId, text);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemDto(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItem(@RequestHeader("X-Sharer-User-Id") Long userId,@RequestParam(defaultValue = "0") Long from, @RequestParam Optional<Long> size) {
        return itemService.getAllUsersItem(from,size,userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,@RequestParam(defaultValue = "0") Long from, @RequestParam Optional<Long> size) {
        return itemService.searchItem(from,size,text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, MapperItem.mapToItem(itemDto));
    }
}
