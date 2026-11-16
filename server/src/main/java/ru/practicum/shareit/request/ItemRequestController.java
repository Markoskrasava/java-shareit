package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postRequest(@RequestBody NewItemRequest newItemRequest, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.postRequest(newItemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsByOther(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestsByOther(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }
}
