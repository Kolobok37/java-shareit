package ru.practicum.shareit.item;

import org.apache.catalina.mapper.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.Storage.BookingDBStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.MapperBooking;
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
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserDBStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemDBStorage itemDBStorage;

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingDBStorage bookingDBStorage;

    @Mock
    private UserDBStorage userStorage;

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemService mockItemService;

    @Test
    void getItemDto_whenItemWithoutBooking_returnItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user, Status.APPROVED);
        //List<Booking> bookings = new ArrayList<>(List.of(booking));
        //item2.setLastBooking(booking);

        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(bookingService.filterBooking(Mockito.anyList(), Mockito.any(State.class))).thenReturn(new ArrayList<>());
        // when(bookingDBStorage.getBookingsOwner(anyLong())).thenReturn(bookings);
        // when(bookingService.filterBooking(Mockito.anyList(), eq(State.PAST))).thenReturn(bookings);

        ItemDto itemDto = mockItemService.getItemDto(1L, 1L);

        assertEquals(MapperItem.mapToItemDto(item2), itemDto);
    }

    @Test
    void getItemDto_whenItemWithBookingRequestByOwner_returnItemWithLastNextBooking() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>(List.of(booking));


        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(bookingService.filterBooking(Mockito.anyList(), Mockito.any(State.class))).thenReturn(bookings);


        ItemDto itemDto = mockItemService.getItemDto(1L, 1L);

        item2.setLastBooking(booking);
        item2.setNextBooking(booking);
        assertEquals(MapperItem.mapToItemDto(item2), itemDto);
    }

    @Test
    void getItemDto_whenItemWithBookingRequestByNotOwner_returnItemWithoutLastNextBooking() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>(List.of(booking));


        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(bookingService.filterBooking(Mockito.anyList(), Mockito.any(State.class))).thenReturn(bookings);

        ItemDto itemDto = mockItemService.getItemDto(1L, 2L);

        assertEquals(MapperItem.mapToItemDto(item2), itemDto);
    }

    @Test
    void createItem_whenWithOutRequestId() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, null, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, null);
        Item item2 = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, null);


        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemDBStorage.createItem(item)).thenReturn(item);

        ItemDto itemDto = mockItemService.createItem(1L, item);

        assertEquals(MapperItem.mapToItemDto(item2), itemDto);
        verifyNoMoreInteractions(requestStorage);

    }

    @Test
    void createItem_whenWithRequestIdNotFound_thenReturnNotFoundException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, null, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);

        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);
        when(requestStorage.getRequest(Mockito.anyLong())).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> mockItemService.createItem(1L, item), "Request is not found.");
        verify(itemDBStorage, never()).createItem(item);
    }

    @Test
    void createItem_whenWithRequestId() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, null, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item itemOld = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Request request = new Request(1L, "request1", LocalDateTime.now(), user, new ArrayList<>());
        Request requestOld = new Request(1L, "request1", LocalDateTime.now(), user, new ArrayList<>());

        ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);


        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);
        when(itemDBStorage.createItem(item)).thenReturn(item);
        when(requestStorage.getRequest(Mockito.anyLong())).thenReturn(Optional.of(request));


        ItemDto itemDto = mockItemService.createItem(1L, item);
        requestOld.getItems().add(item);

        assertEquals(MapperItem.mapToItemDto(itemOld), itemDto);
        verify(requestStorage).createRequest(argumentCaptor.capture());
        Request capturedArgument = argumentCaptor.getValue();
        assertEquals(requestOld, capturedArgument);
    }

    @Test
    void updateItem_whenAllDataIsNew_thenReturnUpdateItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item updateItem = new Item(1L, user, "newItem",
                "newItemIsGood", false, null, null, null, null);
        Item newItem = new Item(1L, user, "newItem",
                "newItemIsGood", false, new ArrayList<>(), null, null, 1L);
        ArgumentCaptor<Item> argumentCaptor = ArgumentCaptor.forClass(Item.class);

        when(itemDBStorage.getItem(anyLong())).thenReturn(item);
        when(itemDBStorage.updateItem(any())).thenReturn(item);


        ItemDto itemDto = mockItemService.updateItem(1L, 1L, updateItem);


        verify(itemDBStorage).updateItem(argumentCaptor.capture());
        Item capturedArgument = argumentCaptor.getValue();

        assertEquals(newItem, capturedArgument);
    }

    @Test
    void updateItem_whenNotAllDataIsNew_thenReturnUpdateItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item updateItem = new Item(null, user, null,
                "newItemIsGood", null, null, null, null, null);
        Item newItem = new Item(1L, user, "item",
                "newItemIsGood", true, new ArrayList<>(), null, null, 1L);
        ArgumentCaptor<Item> argumentCaptor = ArgumentCaptor.forClass(Item.class);

        when(itemDBStorage.getItem(anyLong())).thenReturn(item);
        when(itemDBStorage.updateItem(any())).thenReturn(item);


        ItemDto itemDto = mockItemService.updateItem(1L, 1L, updateItem);


        verify(itemDBStorage).updateItem(argumentCaptor.capture());
        Item capturedArgument = argumentCaptor.getValue();

        assertEquals(newItem, capturedArgument);
    }

    @Test
    void updateItem_whenUserDontHaveAccess_thenReturnUpdateItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item updateItem = new Item(null, user, null,
                "newItemIsGood", null, null, null, null, null);

        when(itemDBStorage.getItem(anyLong())).thenReturn(item);

        assertThrows(NotFoundException.class, () -> mockItemService.updateItem(2L, 1L, updateItem)
                , "User is specified incorrectly");
        verify(itemDBStorage, never()).updateItem(any());
    }

    @Test
    void createComment() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user, Status.APPROVED);
        List<BookingDto> bookings = new ArrayList<>(List.of(MapperBooking.mapToBookingDto(booking)));
        ArgumentCaptor<Comment> argumentCaptor = ArgumentCaptor.forClass(Comment.class);
        Comment comment = new Comment(1L, null, null, null, "text");
        Comment commentNew = new Comment(1L, item, user, LocalDateTime.now(), "text");

        when(itemDBStorage.getItem(anyLong())).thenReturn(item);
        when(bookingService.getBookingByUser(any(), any(), any(), any())).thenReturn(bookings);
        when(itemDBStorage.updateItem(any())).thenReturn(item);
        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDto = mockItemService.createComment(1L, 1L, comment);

        verify(commentRepository).save(argumentCaptor.capture());
        Comment capturedArgument = argumentCaptor.getValue();

        commentNew.setCreated(capturedArgument.getCreated());

        assertEquals(MapperComment.mapToCommentDto(commentNew), MapperComment.mapToCommentDto(capturedArgument));
    }

    @Test
    void createComment_whenNotBeBooking_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);

        Comment comment = new Comment(1L, null, null, null, "text");
        Comment commentNew = new Comment(1L, item, user, LocalDateTime.now(), "text");

        when(itemDBStorage.getItem(anyLong())).thenReturn(item);
        when(bookingService.getBookingByUser(any(), any(), any(), any())).thenReturn(new ArrayList<>());


        assertThrows(ValidationDataException.class, () -> mockItemService.createComment(1L, 1L, comment), "User don't booking this item.");

    }

    @Test
    void getAllUsersItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(1L, user, "item1",
                "itemIsG1", true, new ArrayList<>(), null, null, 1L);
        List<Item> items = new ArrayList<>(List.of(item, item2));

        when(itemDBStorage.getAllUserItems(anyLong())).thenReturn(items);

        List<ItemDto> itemsDto = mockItemService.getAllUsersItem(0L, Optional.empty(), 1L);

        assertEquals(MapperItem.mapToItemDto(item), itemsDto.get(0));
        assertEquals(MapperItem.mapToItemDto(item2), itemsDto.get(1));
    }

    @Test
    void searchItem_whenTextSearchIsBlank_returnEmptyList() {
        List<ItemDto> items = mockItemService.searchItem(1L, Optional.of(5L), "");

        assertTrue(items.isEmpty());
        verify(itemDBStorage, never()).getSearchItem(Mockito.any());
    }

    @Test
    void searchItem_whenItemIsAvailableAndHaveBooking_returnAllItem() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, user, "item1",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(2L, user, "item2",
                "itemIsBed", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user, Status.APPROVED);
        Booking booking2 = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item2, user, Status.APPROVED);
        List<Booking> bookings = new ArrayList<>(List.of(booking, booking2));
        Item itemOldCopy = new Item(1L, user, "item1",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item itemOldCopy2 = new Item(2L, user, "item2",
                "itemIsBed", true, new ArrayList<>(), null, null, 1L);
        itemOldCopy.setNextBooking(booking);
        itemOldCopy.setLastBooking(booking);
        itemOldCopy2.setNextBooking(booking);
        itemOldCopy2.setLastBooking(booking);


        when(itemDBStorage.getSearchItem(anyString())).thenReturn(new ArrayList<>(List.of(item, item2)));
        when(bookingService.filterBooking(anyList(), any(State.class))).thenReturn(bookings);
        when(bookingDBStorage.getBookingsOwner(anyLong())).thenReturn(bookings);
        when(itemDBStorage.getItem(anyLong())).thenReturn(item);


        List<ItemDto> items = mockItemService.searchItem(0L, Optional.of(5L), "Text");

        assertTrue(items.size() == 2);
        assertEquals(MapperItem.mapToItemDto(itemOldCopy), items.get(0));
        assertEquals(MapperItem.mapToItemDto(itemOldCopy2), items.get(1));
    }

    @Test
    void searchItem_whenItemIsAvailableOrNot_returnAvailableItem() {
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item1",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(1L, new User(2L, "alex1", "alex1@gmail.com"), "item2",
                "itemIsBed", false, new ArrayList<>(), null, null, 1L);
        when(itemDBStorage.getSearchItem(anyString())).thenReturn(new ArrayList<>(List.of(item, item2)));


        List<ItemDto> items = mockItemService.searchItem(0L, Optional.of(5L), "Text");

        assertTrue(items.size() == 1);
        assertEquals(MapperItem.mapToItemDto(item), items.get(0));
    }

    @Test
    void searchItem_whenPagingWhitFromAndSize_returnItemBetween() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Item item = new Item(1L, new User(1L, "alex", "alex1@gmail.com"), "item1",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Item item2 = new Item(2L, new User(1L, "alex1", "alex1@gmail.com"), "item2",
                "itemIsBed", true, new ArrayList<>(), null, null, 1L);
        Item item3 = new Item(3L, new User(1L, "alex1", "alex1@gmail.com"), "item3",
                "itemIsBed2", true, new ArrayList<>(), null, null, 1L);
        Item item4 = new Item(4L, new User(1L, "alex1", "alex1@gmail.com"), "item4",
                "itemIsBed3", true, new ArrayList<>(), null, null, 1L);

        when(itemDBStorage.getSearchItem(anyString())).thenReturn(new ArrayList<>(List.of(item, item2, item3, item4)));

        List<ItemDto> items = mockItemService.searchItem(1L, Optional.of(2L), "Text");

        assertTrue(items.size() == 2);
        assertEquals(MapperItem.mapToItemDto(item2), items.get(0));
        assertEquals(MapperItem.mapToItemDto(item3), items.get(1));
    }
}