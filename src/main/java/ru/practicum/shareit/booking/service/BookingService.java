package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto postBooking(Long userId, BookingDto bookingDto);

    BookingDto patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUser(String state, Long userId);

    List<BookingDto> getBookingsByOwner(String state, Long userId);
}
