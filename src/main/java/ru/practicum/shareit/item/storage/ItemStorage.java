package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItem(int itemId);

    Item updateItem(Item item);

    List<Item> getAllUserItems(int userId);

    List<Item> getAllItems();

}
