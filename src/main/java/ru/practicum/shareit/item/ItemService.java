package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Storage.BookingDBStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.MapperComment;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemDBStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDBStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    @Autowired
    private ItemDBStorage itemStorage;

    @Autowired
    private BookingDBStorage bookingDBStorage;

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserDBStorage userStorage;

    @Autowired
    private CommentRepository commentRepository;

    public ItemDto getItemDto(Long itemId, long userId) {
        Item item = updateLastNextBooking(itemStorage.getItem(itemId));
        if (item.getOwner().getId() != userId) {
            item.setNextBooking(null);
            item.setLastBooking(null);
        }
        return MapperItem.mapToItemDto(item);
    }

    public ItemDto createItem(Long userId, Item item) {
        User user = validationUser(userId);
        item.setOwner(user);
        Item newItem = itemStorage.createItem(item);
        return MapperItem.mapToItemDto(newItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        item.setId(itemId);
        User user = validationUser(userId);
        if (itemStorage.getItem(item.getId()).getOwner().getId() != userId) {
            throw new NotFoundException("User is specified incorrectly");
        }
        item.setOwner(user);
        Item oldItem = itemStorage.getItem(itemId);
        if (item.getName() != null && !item.getName().equals(oldItem.getName())) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().equals(oldItem.getDescription())) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && !item.getAvailable() == oldItem.getAvailable()) {
            oldItem.setAvailable(item.getAvailable());
        }
        itemStorage.updateItem(oldItem);

        Item itemEnd = itemStorage.getItem(itemId);
        return MapperItem.mapToItemDto(itemEnd);
    }

    public List<ItemDto> getAllUsersItem(Long userId) {
        validationUser(userId);
        return itemStorage.getAllUserItems(userId).stream().peek(item -> updateLastNextBooking(item)).
                map(item -> MapperItem.mapToItemDto(item)).collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems().stream()
                .filter(item -> item.getName().toLowerCase().indexOf(text.toLowerCase()) != -1 ||
                        item.getDescription().toLowerCase().indexOf(text.toLowerCase()) != -1)
                .filter(item -> item.getAvailable() == true).peek(item -> updateLastNextBooking(item)).
                peek(item -> updateLastNextBooking(item)).map(item -> MapperItem.mapToItemDto(item))
                .collect(Collectors.toList());
    }

    private User validationUser(Long userId) {
        Optional<User> user = userStorage.getAllUser().stream().filter(user1 -> user1.getId() == userId).findFirst();
        if (user.isEmpty()) {
            throw new NotFoundException("User is not specified");
        }
        return user.get();
    }

    private Item updateLastNextBooking(Item item) {
        List<Booking> bookingsNext = bookingService
                .filterBooking(bookingDBStorage.getBookingsOwner(item.getOwner().getId()), "FUTURE")
                .stream().filter(booking -> booking.getItem().getId() == item.getId()).collect(Collectors.toList());
        if (bookingsNext.size() != 0) {
            item.setNextBooking(bookingsNext.stream().max((b1, b2) -> b2.getStart().compareTo(b1.getStart())).get());
            updateItem(item.getOwner().getId(), item.getId(), item);
        }
        List<Booking> bookingsLast = bookingService
                .filterBooking(bookingDBStorage.getBookingsOwner(item.getOwner().getId()), "PAST")
                .stream().filter(booking -> booking.getItem().getId() == item.getId()).collect(Collectors.toList());
        if (bookingsLast.size() != 0) {
            item.setLastBooking(bookingsLast.stream().findFirst().get());
            updateItem(item.getOwner().getId(), item.getId(), item);
        }

        return item;
    }

    private Item getItem(long itemId) {
        return itemStorage.getItem(itemId);
    }

    public CommentDto createComment(Long userId, Long itemId, Comment comment) {
        Item item = getItem(itemId);
        try {
            if (bookingService.getBookingByUser(userId, "PAST").stream()
                    .filter(bookingDto -> bookingDto.getItem().getId() == itemId)
                    .filter(bookingDto -> bookingDto.getBooker().getId() == userId).findFirst().isEmpty()) {
                throw new ValidationDataException("User don't booking this item.");
            }
        } catch (NotFoundException e) {
            throw new ValidationDataException("User don't booking this item.");
        }
        comment.setItem(item);
        comment.setBooker(userStorage.getUser(userId));
        comment.setCreated(LocalDateTime.now());
        item.getComments().add(comment);
        comment = commentRepository.save(comment);
        itemStorage.updateItem(item);
        return MapperComment.mapToCommentDto(comment);
    }
}
