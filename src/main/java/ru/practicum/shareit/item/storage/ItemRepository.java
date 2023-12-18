package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId);
    List<Item> findByNameContainingIgnoreCase(String text);
    List<Item> findByDescriptionContainingIgnoreCase(String text);
}