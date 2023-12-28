package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class MapperBooking {
    public static Booking mapToBooking(BookingDto booking, Item item, User user) {
        return new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
                item, user, booking.getStatus());
    }

    public static Booking mapToBooking(BookingInDto booking, Item item) {
        return new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
                item, null, booking.getStatus());
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                MapperItem.mapToItemBookingDto(booking.getItem()), booking.getBooker(), booking.getStatus());
    }

    public static BookingItemDto mapToBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getBooker().getId(), booking.getStatus());
    }

    public static Booking mapToBooking(BookingItemDto booking, Item item, User user) {
        return new Booking(booking.getId(), booking.getStart(), booking.getEnd(),
                item, user, booking.getStatus());
    }
}

