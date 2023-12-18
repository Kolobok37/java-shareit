package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.MapperRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    RequestService requestService;

    @Test
    @SneakyThrows
    void getRequestsByUser() {
        long userId = 1L;
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).getRequests(userId);
    }

    @Test
    @SneakyThrows
    void getRequest() {
        long requestId = 1L;
        long userId = 1L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).getRequest(requestId, userId);
    }

    @Test
    @SneakyThrows
    void getAllRequests() {
        String from = "1";
        String size = "1";
        long userId = 1L;
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from).param("size", size))
                .andExpect(status().isOk());

        verify(requestService).getAllRequest(userId, Long.valueOf(from), Optional.of(Long.valueOf(size)));
    }

    @Test
    @SneakyThrows
    void createRequest_whenBookingIsValid_thenReturnOk() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(1L, "request1", LocalDateTime.now(), user, new ArrayList<>());
        when(requestService.createRequest(1L, request))
                .thenReturn(MapperRequest.mapToRequestDto(request));

        String result = mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper
                .writeValueAsString(MapperRequest.mapToRequestDto(request)), result);
    }

    @Test
    @SneakyThrows
    void createRequest_whenBookingIsNotValid_thenReturnBedRequest() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(1L, "", LocalDateTime.now(), user, new ArrayList<>());
        when(requestService.createRequest(1L, request))
                .thenReturn(MapperRequest.mapToRequestDto(request));

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verify(requestService, never()).createRequest(1L, request);
    }
}