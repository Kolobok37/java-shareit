package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Storage.BookingDBStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.MapperBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ReservationException;
import ru.practicum.shareit.exception.StatusBookingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    BookingDBStorage bookingStorage;
    ItemStorage itemStorage;
    UserStorage userStorage;

    public BookingDto createBooking(BookingInDto bookingInDto, Long userId) {
        Booking booking = MapperBooking.mapToBooking(bookingInDto, itemStorage.getItem(bookingInDto.getItemId()));
        booking.setStatus(Status.WAITING);
        booking.setBooker(userStorage.getUser(userId));
        validationBookingRequest(booking);
        if (Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new NotFoundException("The owner cannot book the item.");
        }
        bookingStorage.createBooking(booking);
        return updateItemBooking(booking);
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingStorage.getBooking(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return MapperBooking.mapToBookingDto(booking);
        }
        throw new NotFoundException("No access to booking.");
    }

    public BookingDto respondingToRequest(Long bookingId, boolean approved, Long userId) {
        BookingDto bookingDto = getBooking(bookingId, userId);
        Booking booking = MapperBooking.mapToBooking(bookingDto,
                itemStorage.getItem(bookingDto.getItem().getId()), bookingDto.getBooker());
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ReservationException("No access to booking.");
        }
        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new NotFoundException("No access to booking.");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingStorage.updataBooking(booking);
        updateItemBooking(booking);
        booking = bookingStorage.updataBooking(booking);
        return MapperBooking.mapToBookingDto(booking);
    }

    public List<BookingDto> getBookingByUser(Long userId, State state) {
        List<Booking> bookings = filterBooking(bookingStorage.getBookingsUser(userId).stream()
                .peek(this::updateItemBooking)
                .collect(Collectors.toList()),state);
        if (bookings.size() == 0) {
            throw new NotFoundException("User is specified incorrectly");
        }
        return bookings.stream().map(MapperBooking::mapToBookingDto).collect(Collectors.toList());
    }

    public List<BookingDto> getBookingByOwner(Long userId, State state) {
        List<Booking> bookings = filterBooking(bookingStorage.getBookingsOwner(userId), state).stream()
                .peek(this::updateItemBooking)
                .collect(Collectors.toList());
        if (bookings.size() == 0) {
            throw new NotFoundException("There are no bookings");
        }
        return bookings.stream().map(MapperBooking::mapToBookingDto).collect(Collectors.toList());
    }

    private void validationBookingRequest(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())
                || booking.getEnd() == null || booking.getStart() == null) {
            throw new ReservationException("Date error.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ReservationException("Item is already booked or in use.");
        }
    }

    public BookingDto updateItemBooking(Booking booking) {
        Item item = booking.getItem();
        if (item.getNextBooking() != null && (item.getNextBooking().getStart().isBefore(LocalDateTime.now())
                || item.getNextBooking().getStatus().equals(Status.REJECTED))) {
            Optional<Booking> lastBooking = filterBooking(bookingStorage
                    .getBookingsUser(booking.getItem().getOwner().getId()), State.PAST)
                    .stream().filter(booking1 -> Objects.equals(booking1.getItem().getId(), item.getId())).findFirst();
            lastBooking.ifPresent(item::setLastBooking);
            Optional<Booking> nextBooking =
                    filterBooking(bookingStorage.getBookingsUser(booking.getItem().getOwner().getId()), State.FUTURE)
                            .stream().filter(booking1 -> Objects.equals(booking1.getItem().getId(), item.getId()))
                            .min(Comparator.comparing(Booking::getStart));
            if (nextBooking.isPresent()) {
                item.setNextBooking(nextBooking.get());
            } else {
                item.setNextBooking(null);
            }
        }
        if (booking.getStart().isAfter(LocalDateTime.now()) && !booking.getStatus().equals(Status.REJECTED)) {
            if (item.getNextBooking() == null || booking.getStart().isBefore(item.getNextBooking().getStart())) {
                item.setNextBooking(booking);
            }
        } else {
            if ((item.getLastBooking() == null || booking.getStart().isAfter(item.getLastBooking().getStart()))
                    && !booking.getStatus().equals(Status.REJECTED)) {
                item.setLastBooking(booking);
            }
        }
        itemStorage.updateItem(item);
        booking.setItem(item);
        return MapperBooking.mapToBookingDto(booking);
    }

    public List<Booking> filterBooking(List<Booking> bookings, State state) {
        switch (state) {
            case ALL:
                return bookings.stream().sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case WAITING:
                return bookings.stream().filter(booking -> booking.getStatus() == Status.WAITING)
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream().filter(booking -> booking.getStatus() == Status.REJECTED)
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED)
                                || booking.getStatus().equals(Status.WAITING))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .collect(Collectors.toList());
            default:
                throw new StatusBookingException(String.format("Unknown state: %s", state));
        }
    }
}

