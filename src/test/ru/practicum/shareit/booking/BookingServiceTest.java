package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Storage.BookingDBStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.MapperBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ReservationException;
import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemDBStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDBStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ItemDBStorage itemDBStorage;

    @Mock
    private BookingDBStorage bookingDBStorage;

    @Mock
    private UserDBStorage userStorage;

    @InjectMocks
    private BookingService mockBookingService;

    @Test
    void createBooking_whenAllDataCorrect_thenReturnBooking() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(null, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                item, user, Status.WAITING);
        BookingInDto newBooking = new BookingInDto(null,LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),1L,null,null);
        ArgumentCaptor<Booking> argumentCaptor = ArgumentCaptor.forClass(Booking.class);



        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);



       BookingDto bookingDto = mockBookingService.createBooking(newBooking, 2L);

        verify(bookingDBStorage).createBooking(argumentCaptor.capture());
        Booking capturedArgument = argumentCaptor.getValue();
        assertEquals(booking, capturedArgument);
    }

    @Test
    void createBooking_whenOwnerBooking_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        BookingInDto newBooking = new BookingInDto(null,LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),1L,null,null);

        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);

       assertThrows(NotFoundException.class,()->mockBookingService.createBooking(newBooking, 1L),"The owner cannot book the item.");
    }

    @Test
    void createBooking_whenDateIsNotCorrect_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        BookingInDto newBooking = new BookingInDto(null,LocalDateTime.of(2020,1,1,1,1)
                , LocalDateTime.of(2025,1,2,1,1),1L,null,null);

        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);

        assertThrows(ReservationException.class,()->mockBookingService.createBooking(newBooking, 2L),"Date error.");
    }

    @Test
    void createBooking_whenItemNotAvailable_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", false, new ArrayList<>(), null, null, 1L);
        BookingInDto newBooking = new BookingInDto(null,LocalDateTime.of(3020,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),1L,null,null);

        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(userStorage.getUser(Mockito.anyLong())).thenReturn(user);

        assertThrows(ReservationException.class,()->mockBookingService.createBooking(newBooking, 2L),"Item is already booked or in use.");
    }

    @Test
    void respondingToRequest_whenReject_thenBookingDtoReject() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                item, user, Status.WAITING);
        BookingDto booking2 = new BookingDto(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                MapperItem.mapToItemBookingDto(item), user, Status.REJECTED);

        when(bookingDBStorage.getBooking(anyLong())).thenReturn(booking);
        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(bookingDBStorage.updataBooking(any(Booking.class))).thenAnswer(InvocationOnMock->InvocationOnMock.getArgument(0));

        BookingDto bookingDto = mockBookingService.respondingToRequest(1L, false,1L);

        assertEquals(booking2, bookingDto);
    }

    @Test
    void respondingToRequest_whenBookingApproved_thenBookingDtoApprovedWithItemWithNextBooking() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                item, user, Status.WAITING);
        Item item2 = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, booking, 1L);
        BookingDto booking2 = new BookingDto(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                MapperItem.mapToItemBookingDto(item2), user, Status.APPROVED);

        when(bookingDBStorage.getBooking(anyLong())).thenReturn(booking);
        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);
        when(bookingDBStorage.updataBooking(any(Booking.class))).thenAnswer(InvocationOnMock->InvocationOnMock.getArgument(0));

        BookingDto bookingDto = mockBookingService.respondingToRequest(1L, true,1L);

        assertEquals(booking2, bookingDto);
    }

    @Test
    void respondingToRequest_whenBookingStatusNotWaiting_thenExceptionReservationException() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                item, user, Status.APPROVED);
        Item item2 = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, booking, 1L);
        BookingDto booking2 = new BookingDto(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                MapperItem.mapToItemBookingDto(item2), user, Status.APPROVED);

        when(bookingDBStorage.getBooking(anyLong())).thenReturn(booking);
        when(itemDBStorage.getItem(Mockito.anyLong())).thenReturn(item);

        assertThrows(ReservationException.class,
                ()->mockBookingService.respondingToRequest(1L, true,1L),
                "No access to booking.");
        verify(bookingDBStorage,never()).updataBooking(any(Booking.class));

    }

    @Test
    void respondingToRequest_whenRespondingNotOwner_thenExceptionNotFoundException() {
        User user = new User(1L, "alex", "alex@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                item, user, Status.APPROVED);
        Item item2 = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, booking, 1L);
        BookingDto booking2 = new BookingDto(1L, LocalDateTime.of(2025,1,1,1,1)
                , LocalDateTime.of(3025,1,2,1,1),
                MapperItem.mapToItemBookingDto(item2), user, Status.APPROVED);

        when(bookingDBStorage.getBooking(anyLong())).thenReturn(booking);

        assertThrows(NotFoundException.class,
                ()->mockBookingService.respondingToRequest(1L, true,2L),
                "No access to booking.");
        verify(bookingDBStorage,never()).updataBooking(any(Booking.class));
    }

    @Test
    void getBooking_whenRequestOwnerItem_thenReturnBookingDto() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user2, Status.APPROVED);

        when(bookingDBStorage.getBooking(Mockito.anyLong())).thenReturn(booking);

        BookingDto bookingDto = mockBookingService.getBooking(1L, 1L);


        assertEquals(MapperBooking.mapToBookingDto(booking), bookingDto);
    }

    @Test
    void getBooking_whenRequestOwnerBooking_thenReturnBookingDto() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user2, Status.APPROVED);

        when(bookingDBStorage.getBooking(Mockito.anyLong())).thenReturn(booking);

        BookingDto bookingDto = mockBookingService.getBooking(1L, 2L);


        assertEquals(MapperBooking.mapToBookingDto(booking), bookingDto);
    }

    @Test
    void getBooking_whenRequestOtherUser_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user2, Status.APPROVED);

        when(bookingDBStorage.getBooking(Mockito.anyLong())).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> mockBookingService.getBooking(1L, 5L), "No access to booking.");
    }

    @Test
    void getBookingByUser_whenBookingExist_thenReturnListWithSorted() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.MAX,
                item, user2, Status.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user2, Status.APPROVED);


        when(bookingDBStorage.getBookingsUser(Mockito.anyLong()))
                .thenReturn(new ArrayList<>(List.of(booking2,booking)));

        List<BookingDto> bookingsDto = mockBookingService.getBookingByUser(0L, Optional.empty(),2L,State.ALL);

        assertEquals(MapperBooking.mapToBookingDto(booking), bookingsDto.get(0));
        assertEquals(MapperBooking.mapToBookingDto(booking2), bookingsDto.get(1));
    }

    @Test
    void getBookingByUser_whenBookingNotExist_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);


        when(bookingDBStorage.getBookingsUser(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class, () ->
                mockBookingService.getBookingByUser(0L, Optional.empty(),2L,State.ALL),
                "User is specified incorrectly");
    }

    @Test
    void getBookingByOwner_whenBookingExist_thenReturnListWithSorted() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.MAX,
                item, user2, Status.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.MIN, LocalDateTime.MAX,
                item, user2, Status.APPROVED);


        when(bookingDBStorage.getBookingsOwner(Mockito.anyLong()))
                .thenReturn(new ArrayList<>(List.of(booking2,booking)));

        List<BookingDto> bookingsDto = mockBookingService.getBookingByOwner(0L, Optional.empty(),2L,State.ALL);

        assertEquals(MapperBooking.mapToBookingDto(booking), bookingsDto.get(0));
        assertEquals(MapperBooking.mapToBookingDto(booking2), bookingsDto.get(1));
    }

    @Test
    void getBookingByOwner_whenBookingNotExist_thenReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");

        Item item = new Item(1L, user, "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);


        when(bookingDBStorage.getBookingsOwner(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class, () ->
                        mockBookingService.getBookingByOwner(0L, Optional.empty(),2L,State.ALL),
                "There are no bookings");
    }

}