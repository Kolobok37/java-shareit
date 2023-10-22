package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorageInMemory;
import ru.practicum.shareit.user.dto.MapperUser;
import ru.practicum.shareit.user.storage.UserStorageInMemory;

import java.util.ArrayList;
import java.util.List;
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
        validationUser(userId);
        validationItem(item);
        item.setUser(MapperUser.mapToUserDto(userStorageInMemory.getUser(userId)));
        Item newItem = MapperItem.mapToItem(item);
        itemStorage.createItem(newItem);
        return MapperItem.mapToItemDto(newItem);
    }

    public ItemDto updateItem(int userId, int itemId, ItemDto item) {
        item.setId(itemId);
        validationUser(userId);
        if (itemStorage.getItem(item.getId()).getUser().getId() != userId) {
            throw new NotFoundException("User is specified incorrectly");
        }
        item.setUser(MapperUser.mapToUserDto(userStorageInMemory.getUser(userId)));
        Item newItem = MapperItem.mapToItem(item);
        itemStorage.updateItem(newItem);
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

    private void validationUser(int userId) {
        if (userStorageInMemory.getAllUser().stream().filter(user -> user.getId() == userId).findFirst().isEmpty()) {
            throw new NotFoundException("User is not specified");
        }
    }

    private void validationItem(ItemDto item) {
        try {
            if (item.getName().isEmpty()) {
                throw new ValidationException("Name is not valid");
            }
        } catch (NullPointerException e) {
            throw new ValidationException("Name is not valid");
        }
        try {
            if (item.getDescription().isBlank()) {
                throw new ValidationException("Description is not valid");
            }
        } catch (NullPointerException e) {
            throw new ValidationException("Name is not valid");
        }
        try {
            if (item.getAvailable() == null) {
                throw new ValidationException("Available is not valid");
            }
        } catch (NullPointerException e) {
            throw new ValidationException("Available is not valid");
        }
    }
}
