package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
public class RequestController {
    @Autowired()
    RequestService requestService;

    @GetMapping()
    public List<RequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "0") Integer from, @RequestParam Optional<Integer> size) {
        return requestService.getAllRequest(userId, from, size);
    }

    @PostMapping()
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody Request request) {
        return requestService.createRequest(userId, request);
    }
}
