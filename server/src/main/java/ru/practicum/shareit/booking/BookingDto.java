package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Long itemId;

    private ItemShortDto item;

    private UserShortDto booker;

    private BookingStatus status;
}
