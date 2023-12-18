package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.MapperRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.MapperUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestService mockRequestService;

    @Test
    void getRequest_whenRequestExist_ReturnRequestDto() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());

        when(userService.getUser(Mockito.anyLong())).thenReturn(MapperUser.mapToUserDto(user));
        when(requestStorage.getRequest(Mockito.anyLong())).thenReturn(Optional.of(request));

        RequestDto requestDto = mockRequestService.getRequest(1L, 1L);

        assertEquals(MapperRequest.mapToRequestDto(request), requestDto);
    }

    @Test
    void getRequest_whenRequestNotExist_ReturnException() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());

        when(requestStorage.getRequest(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> mockRequestService.getRequest(1L, 1L), "Request is not found.");
    }

    @Test
    void getRequests() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());
        Request request2 = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());
        List<Request> requestList = new ArrayList<>(List.of(request, request2));

        when(userService.getUser(Mockito.anyLong())).thenReturn(MapperUser.mapToUserDto(user));
        when(requestStorage.getRequests(Mockito.anyLong())).thenReturn(requestList);

        List<RequestDto> requestDto = mockRequestService.getRequests(1L);

        assertTrue(requestDto.size() == 2);
        assertEquals(MapperRequest.mapToRequestDto(request), requestDto.get(0));
        assertEquals(MapperRequest.mapToRequestDto(request2), requestDto.get(1));
    }

    @Test
    void createRequest() {
        User user = new User(1L, "alex", "alex@gmail.com");
        Request request = new Request(1L, "request1",
                null, user, new ArrayList<>());
        ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);

        when(userService.getUser(Mockito.anyLong())).thenReturn(MapperUser.mapToUserDto(user));
        when(requestStorage.createRequest(any())).thenReturn(request);


        RequestDto requestDto = mockRequestService.createRequest(1L, request);

        verify(requestStorage).createRequest(argumentCaptor.capture());
        Request capturedArgument = argumentCaptor.getValue();
        assertTrue(capturedArgument.getCreated().isBefore(LocalDateTime.now()));
        assertTrue(capturedArgument.getDescription().equals("request1"));
    }

    @Test
    void getAllRequest() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");
        Request request = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());
        Request request2 = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user, new ArrayList<>());
        Request request3 = new Request(null, "request",
                LocalDateTime.of(2000, 1, 1, 1, 1), user2, new ArrayList<>());

        List<Request> requestList = new ArrayList<>(List.of(request, request2, request3));

        when(requestStorage.getAllRequests()).thenReturn(requestList);

        List<RequestDto> requestDto = mockRequestService.getAllRequest(2L, 0L, Optional.empty());

        assertTrue(requestDto.size() == 2);
        assertEquals(MapperRequest.mapToRequestDto(request), requestDto.get(0));
        assertEquals(MapperRequest.mapToRequestDto(request2), requestDto.get(1));
    }
}