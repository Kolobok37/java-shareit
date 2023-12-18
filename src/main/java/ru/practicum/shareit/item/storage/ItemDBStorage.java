package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.*;
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
        return item.get();
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getSearchItem(String text) {
        Set<Item> n = itemRepository.findByNameContainingIgnoreCase(text).stream().collect(Collectors.toSet());
        Set<Item> d = itemRepository.findByDescriptionContainingIgnoreCase(text).stream().collect(Collectors.toSet());
        Set<Item> a = new HashSet<>();
        a.addAll(n);
        a.addAll(d);
        return a.stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }
}
