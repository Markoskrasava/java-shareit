package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto dto) {
        return requestClient.postRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByOther(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getRequestsByOther(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId) {
        return requestClient.getRequestById(requestId);
    }
}
