package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.exception.StatusBookingException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingInDto bookingInDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingInDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam Optional<Integer> size) {
        return bookingService.getBookingByUser(from, size, userId, toStateEnum(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam Optional<Integer> size) {
        return bookingService.getBookingByOwnerItem(from, size, userId, toStateEnum(state));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto respondingToRequest(@PathVariable Long bookingId, @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.respondingToRequest(bookingId, approved, userId);
    }

    private State toStateEnum(String state) {
        try {
            State stateEnum = State.valueOf(state);
            return stateEnum;
        } catch (IllegalArgumentException e) {
            throw new StatusBookingException(String.format("Unknown state: %s", state));
        }
    }
}
