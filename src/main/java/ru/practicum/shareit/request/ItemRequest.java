package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
