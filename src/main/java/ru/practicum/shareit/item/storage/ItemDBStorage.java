package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Primary
public class ItemDBStorage implements ItemStorage {
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Item not found.");
        }
        return itemRepository.findById(itemId).get();
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.findAll().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
        return items;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
