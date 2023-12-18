package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.request.dto.MapperRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.MapperUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestService {
    @Autowired
    RequestStorage requestStorage;

    @Autowired
    UserService userService;

    public List<RequestDto> getRequests(Long userId) {
        userService.getUser(userId);
        return requestStorage.getRequests(userId).stream()
                .map(r -> MapperRequest.mapToRequestDto(r)).collect(Collectors.toList());
    }

    public RequestDto getRequest(Long requestId, Long userId) {
        Request request = requestStorage.getRequest(requestId)
                .orElseThrow(() -> new NotFoundException("Request is not found."));
        userService.getUser(userId);
        return MapperRequest.mapToRequestDto(request);
    }

    public RequestDto createRequest(Long userId, Request request) {
        request.setCreated(LocalDateTime.now());
        request.setUser(MapperUser.mapToUser(userService.getUser(userId)));
        return MapperRequest.mapToRequestDto(requestStorage.createRequest(request));
    }

    public List<RequestDto> getAllRequest(Long userId, Long from, Optional<Long> size) {
        List<Request> requests = paging(from, size, requestStorage.getAllRequests());
        return requests.stream().filter(r -> r.getUser().getId() != userId).map(r -> MapperRequest.mapToRequestDto(r))
                .collect(Collectors.toList());
    }

    private List<Request> paging(Long from, Optional<Long> size, List<Request> requests) {
        if (from < 0 || size.isPresent() && size.get() < 1) {
            throw new ValidationDataException("Date is not valid.");
        }
        requests = requests.stream().sorted((r1, r2) -> r2.getCreated().compareTo(r1.getCreated()))
                .skip(from).collect(Collectors.toList());
        if (size.isPresent()) {
            requests.stream().limit(size.get());
        }
        return requests;
    }
}
