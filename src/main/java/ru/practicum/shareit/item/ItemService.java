package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageInMemory;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.storage.UserStorageInMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private ItemStorageInMemory itemStorage;
    private UserStorageInMemory userStorageInMemory;

    public ItemDto getItem(int itemId) {
        return MapperItem.mapToItemDto(itemStorage.getItem(itemId));
    }

    public ItemDto createItem(int userId, ItemDto item) {
        User user = validationUser(userId);
        item.setOwner(MapperUser.mapToUserDto(user));
        Item newItem = MapperItem.mapToItem(item);
        itemStorage.createItem(newItem);
        return MapperItem.mapToItemDto(newItem);
    }

    public ItemDto updateItem(int userId, int itemId, ItemDto item) {
        item.setId(itemId);
        User user = validationUser(userId);
        if (itemStorage.getItem(item.getId()).getOwner().getId() != userId) {
            throw new NotFoundException("User is specified incorrectly");
        }
        item.setOwner(MapperUser.mapToUserDto(user));
        Item newItem = MapperItem.mapToItem(item);
        Item oldItem = itemStorage.getItem(itemId);
        if (newItem.getName() != null && !newItem.getName().equals(oldItem.getName())) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().equals(oldItem.getDescription())) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null && !newItem.getAvailable() == oldItem.getAvailable()) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        itemStorage.updateItem(oldItem);
        return MapperItem.mapToItemDto(itemStorage.getItem(itemId));
    }

    public List<ItemDto> getAllUsersItem(int userId) {
        validationUser(userId);
        return itemStorage.getAllUserItems(userId).stream()
                .map(item -> MapperItem.mapToItemDto(item)).collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems().stream()
                .filter(item -> item.getName().toLowerCase().indexOf(text.toLowerCase()) != -1 ||
                        item.getDescription().toLowerCase().indexOf(text.toLowerCase()) != -1)
                .filter(item -> item.getAvailable() == true)
                .map(item -> MapperItem.mapToItemDto(item))
                .collect(Collectors.toList());
    }

    private User validationUser(int userId) {
        Optional<User> user = userStorageInMemory.getAllUser().stream().filter(user1 -> user1.getId() == userId).findFirst();
        if (user.isEmpty()) {
            throw new NotFoundException("User is not specified");
        }
        return user.get();
    }
}
