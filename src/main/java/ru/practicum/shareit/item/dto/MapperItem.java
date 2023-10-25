package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.MapperUser;

public class MapperItem {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), MapperUser.mapToUserDto(item.getOwner()),
                item.getName(), item.getDescription(), item.getAvailable(),
                item.getReservation(), item.getReviews(), item.getRequest());
    }

    public static Item mapToItem(ItemDto item) {
        return new Item(item.getId(), MapperUser.mapToUser(item.getOwner()),
                item.getName(), item.getDescription(), item.getAvailable(),
                item.getReservation(), item.getReviews(), item.getRequest());
    }
}
