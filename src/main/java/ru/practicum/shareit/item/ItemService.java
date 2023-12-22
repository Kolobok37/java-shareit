package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Paging;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
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
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDBStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private RequestStorage requestStorage;

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
        User user = userStorage.getUser(userId);
        item.setOwner(user);
        if (item.getRequestId() != null) {
            Request request = requestStorage.getRequest(item.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request is not found."));
            Item newItem = itemStorage.createItem(item);
            List<Item> items = request.getItems();
            items.add(newItem);
            request.setItems(items);
            requestStorage.createRequest(request);
            return MapperItem.mapToItemDto(newItem);
        }
        return MapperItem.mapToItemDto(itemStorage.createItem(item));
    }

    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        item.setId(itemId);
        if (!Objects.equals(itemStorage.getItem(item.getId()).getOwner().getId(), userId)) {
            throw new NotFoundException("User is specified incorrectly");
        }
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
        return MapperItem.mapToItemDto(itemStorage.updateItem(oldItem));
    }

    public List<ItemDto> getAllUsersItem(Integer from, Optional<Integer> size, Long userId) {
        userStorage.getUser(userId);
        return itemStorage.getAllUserItems(userId, Paging.paging(from, size)).stream().peek(this::updateLastNextBooking)
                .map(MapperItem::mapToItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(Integer from, Optional<Integer> size, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getSearchItem(text, Paging.paging(from, size)).stream()
                .filter(Item::getAvailable)
                .peek(this::updateLastNextBooking)
                .map(MapperItem::mapToItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(Long userId, Long itemId, Comment comment) {
        Item item = getItem(itemId);
        try {
            if (bookingService.getBookingByUser(Integer.valueOf(0), Optional.empty(), userId, State.PAST).stream()
                    .filter(bookingDto -> Objects.equals(bookingDto.getItem().getId(), itemId))
                    .findFirst().isEmpty()) {
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

    private Item updateLastNextBooking(Item item) {
        List<Booking> bookingsNext = bookingDBStorage
                .getBookingsOwnerItemByTime(item.getOwner().getId(), State.FUTURE, Pageable.unpaged())
                .stream().filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                .collect(Collectors.toList());
        if (bookingsNext.size() != 0) {
            item.setNextBooking(bookingsNext.stream().max((b1, b2) -> b2.getStart().compareTo(b1.getStart())).get());
            updateItem(item.getOwner().getId(), item.getId(), item);
        }
        List<Booking> bookingsLast = bookingDBStorage
                .getBookingsOwnerItemByTime(item.getOwner().getId(), State.PAST, Pageable.unpaged())
                .stream().filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                .collect(Collectors.toList());
        if (bookingsLast.size() != 0) {
            item.setLastBooking(bookingsLast.stream().findFirst().get());
            updateItem(item.getOwner().getId(), item.getId(), item);
        }
        return item;
    }

    private Item getItem(long itemId) {
        return itemStorage.getItem(itemId);
    }

    private List<Item> paging(Long from, Optional<Long> size, List<Item> requests) {
        if (from < 0 || size.isPresent() && size.get() < 1) {
            throw new ValidationDataException("Date is not valid.");
        }
        requests = requests.stream()
                .skip(from).collect(Collectors.toList());
        if (size.isPresent()) {
            requests = requests.stream().limit(size.get()).collect(Collectors.toList());
        }
        return requests;
    }
}
