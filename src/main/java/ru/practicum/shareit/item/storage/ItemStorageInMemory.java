package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemStorageInMemory implements ItemStorage {
    private HashMap<Integer, Item> itemStorage;
    private int id;

    public ItemStorageInMemory(HashMap<Integer, Item> itemStorage) {
        this.itemStorage = itemStorage;
        id = 1;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(id);
        return itemStorage.put(id++, item);
    }

    @Override
    public Item getItem(int itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = itemStorage.get(item.getId());
        if (item.getName() != null && !item.getName().equals(oldItem.getName())) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().equals(oldItem.getDescription())) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && !item.getAvailable() == oldItem.getAvailable()) {
            oldItem.setAvailable(item.getAvailable());
        }
        itemStorage.put(oldItem.getId(), oldItem);
        return oldItem;
    }

    @Override
    public List<Item> getAllUserItems(int userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        return itemStorage.values().stream().collect(Collectors.toList());
    }
}
