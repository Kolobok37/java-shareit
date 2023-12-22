package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Paging;
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

    public List<RequestDto> getAllRequest(Long userId, Integer from, Optional<Integer> size) {
        Page<Request> requests = requestStorage.getAllRequests(Paging.paging(from, size));
        return requests.stream().filter(r -> !r.getUser().getId().equals(userId)).map(r -> MapperRequest.mapToRequestDto(r))
                .collect(Collectors.toList());
    }


    private Pageable paging(Integer from, Optional<Integer> size){
        Pageable pageable;
        if (from < 0 || size.isPresent() && size.get() < 1) {
            throw new ValidationDataException("Date is not valid.");
        }
        if (size.isPresent()) {
            pageable = PageRequest.of(from/size.get(), size.get(),Sort.by("created").descending());
        }
        else
            pageable = PageRequest.of(from,100, Sort.by("created").descending());
        return pageable;
    }
}
