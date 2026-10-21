package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.postBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto acceptingBooking(@PathVariable Long bookingId, @RequestParam Boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByUser(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByOwner(state, userId);
    }
}
