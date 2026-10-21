package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

    @Data
    public class BookingDto {
        private Long id;

        @NotNull
        private LocalDateTime start;

        @NotNull
        private LocalDateTime end;

        @NotNull
        private Item item;

        private User booker;

        private BookingStatus status;
    }
