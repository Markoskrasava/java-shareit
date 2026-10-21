package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.UserShortDto;

import java.util.function.Function;

@Component
public class BookingMapper implements Function<Booking, BookingDto> {

    @Override
    public BookingDto apply(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem().getId());
        dto.setItem(new ItemShortDto(
                booking.getItem().getId(),
                booking.getItem().getName()));
        dto.setBooker(new UserShortDto(
                booking.getBooker().getId(),
                booking.getBooker().getName()));
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
