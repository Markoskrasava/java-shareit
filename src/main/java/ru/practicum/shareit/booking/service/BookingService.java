package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto postBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Начальная дата должна быть раньше конечной");
        }

        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Начальная дата не должна быть равна конечной");
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));
        if (item.getAvailable() == false) {
            throw new ValidationException("Вещь недоступна");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ValidationException("Нельзя бронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.apply(saved);
    }

    @Transactional
    public BookingDto patchBooking(Long bookingId, Boolean approved, Long userId) {
        if (approved == null) {
            throw new ValidationException("Параметр approved обязателен");
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Такого бронирования не существует"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Бронирование не найдено");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.apply(saved);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NoSuchElementException("Такого бронирования не существует"));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NoSuchElementException("Просматривать бронирование может только либо его автор, либо владелец вещи");
        }

        return bookingMapper.apply(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        if (state.isBlank()) {
            state = "ALL";
        }

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> filtered = switch (state.toUpperCase()) {
            case "ALL" -> bookings;
            case "CURRENT" -> bookings.stream()
                    .filter(b -> !b.getStart().isAfter(now) && !b.getEnd().isBefore(now))
                    .toList();
            case "PAST" -> bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .toList();
            case "FUTURE" -> bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .toList();
            case "WAITING" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .toList();
            case "REJECTED" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .toList();
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return filtered.stream()
                .map(bookingMapper::apply)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        if (state.isBlank()) {
            state = "ALL";
        }

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> filtered = switch (state.toUpperCase()) {
            case "ALL" -> bookings;
            case "CURRENT" -> bookings.stream()
                    .filter(b -> !b.getStart().isAfter(now) && !b.getEnd().isBefore(now))
                    .toList();
            case "PAST" -> bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .toList();
            case "FUTURE" -> bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .toList();
            case "WAITING" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .toList();
            case "REJECTED" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .toList();
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return filtered.stream()
                .map(bookingMapper::apply)
                .collect(Collectors.toList());
    }
}
