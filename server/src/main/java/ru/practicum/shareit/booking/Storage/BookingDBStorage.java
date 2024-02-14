package ru.practicum.shareit.booking.Storage;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class BookingDBStorage {
    BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking not found.");
        }
        return booking.get();
    }

    public Booking updataBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsUser(Long userId, Pageable pageable) {
        return bookingRepository.findByBooker_Id(userId, pageable).stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
    }

    public List<Booking> getBookingsUserByStatus(Long userId, State status, Pageable pageable) {
        return bookingRepository.findBookingsUserByStatus(userId, status.toString(), pageable);
    }

    public List<Booking> getBookingsUserByTime(Long userId, State state, Pageable pageable) {
        if (state.equals(State.FUTURE)) {
            return bookingRepository.findBookingsUserByFuture(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        } else if (state.equals(State.PAST)) {
            return bookingRepository.findBookingsUserByPast(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        } else if (state.equals(State.CURRENT)) {
            return bookingRepository.findBookingsUserByCurrent(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        }
        throw new NotFoundException("Used failed method");
    }

    public List<Booking> getBookingsUserAll(Long userId, Pageable paging) {
        return bookingRepository.findByBooker_Id(userId, paging).stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
    }

    public List<Booking> getBookingsOwnerItemByStatus(Long userId, State state, Pageable pageable) {
        return bookingRepository.findBookingsOwnerItemByStatus(userId, state.toString(), pageable).stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
    }

    public List<Booking> getBookingsOwnerItemByTime(Long userId, State state, Pageable pageable) {
        if (state.equals(State.FUTURE)) {
            return bookingRepository.findBookingsOwnerItemByFuture(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        } else if (state.equals(State.PAST)) {
            return bookingRepository.findBookingsOwnerItemByPast(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        } else if (state.equals(State.CURRENT)) {
            return bookingRepository.findBookingsOwnerItemByCurrent(userId, pageable).stream()
                    .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
        }
        throw new NotFoundException("Used failed method");
    }

    public List<Booking> getBookingsOwnerItemAll(Long userId, Pageable pageable) {
        return bookingRepository.findByItemOwnerId(userId, pageable).stream()
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())).collect(Collectors.toList());
    }
}