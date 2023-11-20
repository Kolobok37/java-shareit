package ru.practicum.shareit.booking.Storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
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

    public List<Booking> getBookingsUser(Long userId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getBooker().getId() == userId).collect(Collectors.toList());
    }

    public List<Booking> getBookingsOwner(Long userId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().getOwner().getId() == userId).collect(Collectors.toList());
    }
}
