package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItem(Long itemId);

    Item updateItem(Item item);

    List<Item> getAllUserItems(Long userId, Pageable pageable);

    List<Item> getAllItems(Pageable pageable);
}
