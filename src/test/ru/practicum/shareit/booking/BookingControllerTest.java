package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.MapperBooking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;


    @Test
    @SneakyThrows
    void createBooking_whenBookingIsValid_thenReturnOk() {
        UserDto user =new UserDto(1L,"alex","alex@gmail.com");
        Item item = new Item(null, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        BookingInDto booking = new BookingInDto(1L, LocalDateTime.now(),LocalDateTime.now(),1L,user,Status.APPROVED);

        when(bookingService.createBooking(booking, 1L))
                .thenReturn(MapperBooking.mapToBookingDto(MapperBooking.mapToBooking(booking,item)));

        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper
                 .writeValueAsString(MapperBooking.mapToBookingDto(MapperBooking.mapToBooking(booking,item))), result);
    }

    @Test
    @SneakyThrows
    void createBooking_whenBookingIsNotValid_thenReturnBedRequest() {
        UserDto user =new UserDto(1L,"alex","alex@gmail.com");
        Item item = new Item(null, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null, 1L);
        BookingInDto booking = new BookingInDto(1L, null,LocalDateTime.now(),1L,user,Status.APPROVED);

        when(bookingService.createBooking(booking, 1L))
                .thenReturn(MapperBooking.mapToBookingDto(MapperBooking.mapToBooking(booking,item)));

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest()).andDo(print());

        verify(bookingService,never()).createBooking(booking,1L);
    }

    @Test
    @SneakyThrows
    void getBooking() {
        long bookingId =1L;
        long userId =1L;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).getBooking(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void getBookingByUser_whenStateIsInitialized_thenReturnOkAndBooking() {
        long bookingId =1L;
        long userId =1L;
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/bookings", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from).param("size", size)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

        verify(bookingService).getBookingByUser(Integer.valueOf(from), Optional.of(Integer.valueOf(size)),userId, State.CURRENT);
    }

    @Test
    @SneakyThrows
    void getBookingByUser_whenStateIsNotInitialized_thenReturnOkAndAllBooking() {
        long bookingId =1L;
        long userId =1L;
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/bookings", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from).param("size", size))
                .andExpect(status().isOk());

        verify(bookingService).getBookingByUser(Integer.valueOf(from), Optional.of(Integer.valueOf(size)),userId, State.ALL);
    }

    @Test
    @SneakyThrows
    void getBookingByUser_whenStateIsDontCorrect_thenReturnBedRequest() {
        long bookingId =1L;
        long userId =1L;
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/bookings", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from).param("size", size)
                        .param("state", "Bed state"))
                .andExpect(status().isBadRequest());

        verify(bookingService,never())
            .getBookingByUser(Integer.valueOf(from), Optional.of(Integer.valueOf(size)),userId, State.ALL);
    }

    @Test
    @SneakyThrows
    void getBookingByOwner() {
        long bookingId =1L;
        long userId =1L;
        String from = "1";
        String size = "1";
        mockMvc.perform(get("/bookings/owner", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from).param("size", size)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

       // verify(bookingService).getBookingByOwner(Integer.valueOf(from), Optional.of(Integer.valueOf(size)),userId, State.CURRENT);
    }

    @Test
    @SneakyThrows
    void respondingToRequest() {
        User user =new User(1L,"alex","alex@gmail.com");
        ItemBookingDto item = new ItemBookingDto(null, new User(1L, "alex", "alex1@gmail.com"), "item",
                "itemIsGood", true, new ArrayList<>(), null, null);
        BookingDto booking = new BookingDto(1L,LocalDateTime.now(),LocalDateTime.now(), item,user,Status.APPROVED);
        when(bookingService.respondingToRequest(1L,true, 1L)).thenReturn(booking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}",1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(booking))
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking),result);
    }
}