package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.MapperBooking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperComment;
import ru.practicum.shareit.item.model.Item;

public class MapperItem {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto(item.getId(), item.getOwner(),
                item.getName(), item.getDescription(), item.getAvailable(),
                MapperComment.mapToCommentDto(item.getComments()), null, null,item.getRequestId());
        if (item.getLastBooking() != null) {
            itemDto.setLastBooking(MapperBooking.mapToBookingItemDto(item.getLastBooking()));
        }
        if (item.getNextBooking() != null) {
            itemDto.setNextBooking(MapperBooking.mapToBookingItemDto(item.getNextBooking()));
        }
        return itemDto;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item(itemDto.getId(), itemDto.getOwner(),
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null,
                null,
                null,itemDto.getRequestId());
        return item;
    }

    public static ItemBookingDto mapToItemBookingDto(Item item) {
        ItemBookingDto itemBookingDtoDto = new ItemBookingDto(item.getId(), item.getOwner(),
                item.getName(), item.getDescription(), item.getAvailable(),
                MapperComment.mapToCommentDto(item.getComments()), null, null);
        if (item.getLastBooking() != null) {
            itemBookingDtoDto.setLastBookingId(item.getLastBooking().getId());
        }
        if (item.getNextBooking() != null) {
            itemBookingDtoDto.setNextBookingId(item.getNextBooking().getId());
        }
        return itemBookingDtoDto;
    }
}
