package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemory implements ItemStorage {
    private HashMap<Long, Item> itemStorage;
    private Long id;

    public ItemStorageInMemory(HashMap<Long, Item> itemStorage) {
        this.itemStorage = itemStorage;
        id = (long) 1;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(id);
        return itemStorage.put(id++, item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(itemStorage.values());
    }
}
